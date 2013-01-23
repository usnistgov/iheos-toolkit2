package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.common.coder.Base64Coder;
import gov.nist.toolkit.docref.Mtom;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.XPathEvaluator;

/**
 * Creates a mapping from DocumentEntry.id (used in metadata) to Document.cid 
 * (used in Document to Part mapping) and makes this information
 * available to later stages.
 * @author bill
 *
 */
public class DocumentAttachmentMapper  extends MessageValidator {
	
	// maps DocumentEntry.id to Document.cid
	//
	// The Document.cid is used without the cid: prefix
	//
	// this information comes out of the following structure in the XOP format XML
    //	<xdsb:Document id="urn:uuid:0f8dc31c-2e0d-41f8-bd0e-d68d43bdf4a2">
    //      <xop:Include href="cid:1.urn:uuid:1049A8083359E423AE1282249523329@apache.org" 
    //          xmlns:xop="http://www.w3.org/2004/08/xop/include" />
    //  </xdsb:Document>
	Map<String, String> docIds= new HashMap<String, String>();
	
	// maps DocumentEntry.id to Document contents
	Map<String, StoredDocumentInt> storedDocuments= new HashMap<String, StoredDocumentInt>();
//	Map<String, byte[]> docContents = new HashMap<String, byte[]>();
//	Map<String, String> docContentType = new HashMap<String, String>();
//	Map<String, String> docCharset = new HashMap<String, String>();
	
	// For a particular document, either:
	//    its id will show up in docIds because it was an XOP optimized attachment
	//    to get the content you need the map of content ids to contents in class MultipartContainer
	// OR
	//    its id will show up in docContents because it was an XOP un-optimized content
	//    and docContents has the content
	
	OMElement xml;
	
	public StoredDocumentInt getStoredDocumentForDocumentId(String docId) throws Exception {
		if (storedDocuments.containsKey(docId))
			return storedDocuments.get(docId);
		throw new Exception("Document " + docId + " not part of message, available ids are " + storedDocuments.keySet());
	}
	
	public Set<String> getIds() {
		Set<String> ids = new HashSet<String>();
		for (String id : storedDocuments.keySet())
			ids.add(id);
		for (String id : docIds.keySet())
			ids.add(id);
		return ids;
	}
	
	public String getDocumentContentsIdForDocumentId(String docId) {
		return docIds.get(docId);
	}
	
	public DocumentAttachmentMapper(ValidationContext vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}

	public DocumentAttachmentMapper(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		
		XPathEvaluator eval = new XPathEvaluator();
		try {
			@SuppressWarnings("unchecked")
			List<OMNode> node_list = eval.evaluateXpath("//*[local-name()='Document']", xml, null);
			for (OMNode node : node_list) {
				OMElement docEle;
				
				if (node instanceof OMElement)
					docEle = (OMElement) node;
				else {
					// not sure what this could be - busted Xpath???
					continue;
				}
				
				String id = docEle.getAttributeValue(MetadataSupport.id_qname);
				er.detail("Parsing Document id=\"" + id + "\"");
				List<OMElement> includes = MetadataSupport.childrenWithLocalName(docEle, "Include");
				
				// Either XOP Include pointing to another mulipart Part is present or 
				// document contents is present as text of Document element with base64 encoding
				if (includes.size() == 1) {
					// XOP
					er.detail("Optimized MTOM/XOP format found. Include points to Multipart Part where content should be found");
					OMElement include = includes.get(0);
					String cid = include.getAttributeValue(MetadataSupport.href_qname);
					if (cid == null || cid.equals("")) {
						er.err(XdsErrorCode.Code.XDSRepositoryError, "No href attribute on XOP Include", this, Mtom.XOP_include);
					} else if (!cid.startsWith("cid:")) {
						er.err(XdsErrorCode.Code.XDSRepositoryError, "XOP Include href attribute must have prefix cid:", this, Mtom.XOP_include);
					} else {
						String aid = cid;
						if (aid.startsWith("cid:"))
							aid = aid.substring(4);
						docIds.put(id, aid);
						er.detail("Maps to attachment " + aid);
					}
					
					String includeNSURI = include.getNamespace().getNamespaceURI();
					if (!"http://www.w3.org/2004/08/xop/include".equals(includeNSURI))
						er.err(XdsErrorCode.Code.XDSRepositoryError, "Wrong XML namespace on Include element.  Found " + includeNSURI + " but it should have been http://www.w3.org/2004/08/xop/include", this, Mtom.XOP_include);
					
					include.detach();   // later, Schema will not expect the XOP format Include so detach from XML tree
					
				} else if (includes.size() == 0) {
					// no XOP Include (technically an Un-optimized MTOM/XOP encoding
					er.detail("Unoptimized MTOM/XOP format found. No include attribute. Contents should be base64 encoded and found as the value of the Document element");
					String xopContentType = docEle.getAttributeValue(MetadataSupport.xop_content_type_qnamens);
					String contentType = "text/plain";
					String charset = "UTF-8";
					if (xopContentType != null) {
						HttpHeader hdr = new HttpHeader("Content-Type: " + xopContentType);
						contentType = hdr.getValue();
						if (hdr.hasParam("charset"))
							charset = hdr.getParam("charset");
					}
					//er.detail("Un-optimized MTOM/XOP encoding found");
					String base64Contents = docEle.getText();
					if (base64Contents == null || base64Contents.equals("")) {
						er.err(XdsErrorCode.Code.XDSRepositoryError, "Document contents not a XOP Include pointing to a separate Part and not inline Base64", this, Mtom.XOP_include);
					} else {
						byte[] contents = Base64Coder.decode(base64Contents);
						StoredDocumentInt sd = new StoredDocumentInt();
						storedDocuments.put(id, sd);
						sd.content = contents;
						sd.mimeType = contentType;
						sd.charset = charset;
						er.detail("Found " + contents.length + " bytes of content");
					}
				} else {
					// multiple Include????
					er.err(XdsErrorCode.Code.XDSRepositoryError, "Multiple Include elements found under Document element", this, Mtom.XOP_include);
				}
			}
		} catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRepositoryError, e);
			return;
		}
	}

}
