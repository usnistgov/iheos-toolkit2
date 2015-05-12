package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valregmetadata.object.Classification;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class CodeValidationBase {
	Metadata m;
	OMElement codes = null;
	List<String> assigning_authorities;
	HashMap<String, String> mime_map;  // mime => ext
	HashMap<String, String> ext_map;   // ext => mime
	Exception startUpError = null;
	ValidationContext vc = null;
	
	static String ADConfigError = "ITI TF-3: 4.1.10";
	static Logger logger = Logger.getLogger(CodeValidationBase.class);


	CodeValidationBase() {}
	
	CodeValidationBase(int ignore) throws XdsInternalException {
		loadCodes();
	}
	
	public void setValidationContext(ValidationContext vc) throws XdsInternalException {
		this.vc = vc;
		loadCodes();
	}
	
	void loadCodes() throws XdsInternalException {
		if (codes != null)
			return;
		System.out.println("Loading Codes");
		String fileCodesLocation = null;
		
		if (vc != null)
			fileCodesLocation = vc.getCodesFilename();
		if (fileCodesLocation == null)
			fileCodesLocation = System.getenv("XDSCodesFile");
		if (fileCodesLocation == null)
			fileCodesLocation = System.getProperty("XDSCodesFile");
		
		String localCodesLocation = "http://localhost:9080/xdsref/codes/codes.xml";
		String globalCodesLocation = "http://ihexds.nist.gov:9080/xdsref/codes/codes.xml";
		
		String codes_string = null;
		String from = null;

		if (fileCodesLocation != null) {
			try {
				codes_string = Io.getStringFromInputStream(new FileInputStream(new File(fileCodesLocation)));
				from = fileCodesLocation;
			}
			catch (Exception e) {
				throw new XdsInternalException("codes.xml file cannot be loaded from " + fileCodesLocation, e);
			}
		}
		else {

			try {
				codes_string = HttpClient.httpGet(localCodesLocation);
				from = localCodesLocation;
			}
			catch (Exception e1) {
				logger.warn("Cannot contact localhost: " + ExceptionUtil.exception_details(e1));
				try {
					codes_string = HttpClient.httpGet(globalCodesLocation);
					from = globalCodesLocation;
				}
				catch (Exception e) {
					throw new XdsInternalException("CodeValidation: Unable to retrieve code configuration file " + globalCodesLocation +
							"\n" + e.getMessage());
				}
			}
		}
		if (codes_string == null) 
			throw new XdsInternalException("CodeValidation.init(): GET codes.xml returned NULL from " + from);
		if (codes_string.equals("")) 
			throw new XdsInternalException("CodeValidation.init(): GET codes.xml returned enpty from " + from);

		logger.info("Codes loaded from " + from);
		
		codes = Util.parse_xml(codes_string);
		if (codes == null)
			throw new XdsInternalException("CodeValidation: cannot parse code configuration file from " + from);

		assigning_authorities = new ArrayList<String>();
		for (OMElement aa_ele : MetadataSupport.childrenWithLocalName(codes, "AssigningAuthority")) 
		{
			this.assigning_authorities.add(aa_ele.getAttributeValue(MetadataSupport.id_qname));
		}

		build_mime_map();
	}

	void build_mime_map() throws XdsInternalException {
		QName name_att_qname = new QName("name");
		QName code_att_qname = new QName("code");
		QName ext_att_qname = new QName("ext");
		OMElement mime_type_section = null;
		for(@SuppressWarnings("unchecked")
		Iterator<OMElement> it=codes.getChildrenWithName(new QName("CodeType")); it.hasNext();  ) {
			OMElement ct = it.next();
			if (ct.getAttributeValue(name_att_qname).equals("mimeType")) {
				mime_type_section = ct;
				break;
			}
		}
		if (mime_type_section == null) throw new XdsInternalException("CodeValidation2.java: Configuration Error: Cannot find mime type table");

		mime_map = new HashMap<String, String>();
		ext_map = new HashMap<String, String>();

		for(@SuppressWarnings("unchecked")
		Iterator<OMElement> it=mime_type_section.getChildElements(); it.hasNext();  ) {
			OMElement code_ele = it.next();
			String mime_type = code_ele.getAttributeValue(code_att_qname);
			String ext = code_ele.getAttributeValue(ext_att_qname);
			mime_map.put(mime_type, ext);
			ext_map.put(ext, mime_type);
		}
	}

	public List<String> getAssigningAuthorities() {
		try {
			loadCodes();
		} catch (Exception e) {
			System.out.println("loading codes: " + e.getMessage());
		}
		return assigning_authorities;
	}
	
	String[] assocClassifications = { 
			"XFRM", "APND", "RPLC", "XFRM_RPLC"
	};

	public boolean isValidMimeType(String mime_type) {
		return mime_map.containsKey(mime_type);
	}

	public Collection<String> getKnownFileExtensions() {
		return ext_map.keySet();
	}

	public String getMimeTypeForExt(String ext) {
		return ext_map.get(ext);
	}

	public String getExtForMimeType(String mime_type) {
		return mime_map.get(mime_type);
	}

	// next 3 copied from SubmissionStructure.java
	String objectType(String id) {
		if (id == null)
			return "null";
		if (m.getSubmissionSetIds().contains(id))
			return "SubmissionSet";
		if (m.getExtrinsicObjectIds().contains(id))
			return "DocumentEntry";
		if (m.getFolderIds().contains(id))
			return "Folder";
		if (m.getAssociationIds().contains(id))
			return "Association";
		return "Unknown";
	}
	
	String objectDescription(String id) {
		return objectType(id) + "(" + id + ")";
	}
	
	String objectDescription(OMElement ele) {
		String objectType = objectType(m.getId(ele));
		if (objectType.equals("Unknown"))
			objectType = ele.getLocalName();
		return objectType + "(" + m.getId(ele) + ")";
	}
	
	String getObjectTypeById(ErrorRecorder er, String id) {
		try {
			return m.getObjectTypeById(id);
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
			return null;
		}
	}
	
	OMElement getObjectById(ErrorRecorder er, String id) {
		try {
			return m.getObjectById(id);
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
			return null;
		}
	}
	
	String getSimpleAssocType(ErrorRecorder er, OMElement assoc) {
		try {
			return m.getSimpleAssocType(assoc);
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
			return null;
		}
	}

	void cannotValidate(ErrorRecorder er, Classification c) {
		er.err(XdsErrorCode.Code.XDSRegistryMetadataError, c.identifyingString() + ": cannot validate code - error parsing Classification", this, "ebRIM section 4.3");
	}

	public void run(ErrorRecorder er) {
		if (startUpError != null) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, startUpError);
			return;
		}
		
		er.sectionHeading("Evaluating use of Affinity Domain coding");
		
	
		List<String> all_object_ids = m.getObjectIds(m.getAllObjects());
	
		for (String obj_id : all_object_ids) {
			List<OMElement> classifications = null;
	
			try {
				classifications = m.getClassifications(obj_id);
			} catch (MetadataException e) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
				continue;
			}
	
			for (OMElement cl_ele : classifications) {
	
				Classification cl = null;
				try {
					cl = new Classification(m, cl_ele);
				} catch (XdsInternalException e) {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
					continue;
				}
				validate(er, cl);
	
				validateAssocClassifications(er, cl);
	
			}
		}
	
		for (OMElement doc_ele : m.getExtrinsicObjects()) {
			String mime_type = doc_ele.getAttributeValue(MetadataSupport.mime_type_qname);
			if ( !isValidMimeType(mime_type)) {
				if (vc.isXDM || vc.isXDR) 
					er.detail("Mime type, " + mime_type + ", is not available in this Affinity Domain");
				else
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Mime type, " + mime_type + ", is not available in this Affinity Domain", this, ADConfigError);
			} 
		}
	}

	// if classified object is an Association, only some types of Associations can
	// accept an associationDocumenation classification
	void validateAssocClassifications(ErrorRecorder er, Classification cl) {

		String classification_type = cl.getClassificationScheme();

		if (classification_type == null || !classification_type.equals(MetadataSupport.XDSAssociationDocumentation_uuid))
			return;  // not associationDocumenation classification

		String classified_object_id = cl.parent_id();
		String classified_object_type = getObjectTypeById(er, classified_object_id);
		if (classified_object_type == null)
			return;

		if ( !classified_object_type.equals("Association")) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(cl.getOwnerId()) + ": associationDocumentation Classification (" + MetadataSupport.XDSAssociationDocumentation_uuid + ") can only be used on Associations", this, "ITI TF-3: 4.1.6.1");
			return;
		}

		String assoc_id = classified_object_id;
		OMElement assoc_ele = getObjectById(er, assoc_id);
		if (assoc_ele == null) 
			return;
		String assoc_type = getSimpleAssocType(er, assoc_ele);
		for (int i=0; i<assocClassifications.length; i++) {
			String a = assocClassifications[i];
			if (a.equals(assoc_type))
				return;
		}
		er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(assoc_ele) + ": Association Type " + assoc_type + " cannot have an associationDocumentation classification", this, "ITI TF-3: 4.1.6.1");
	}
	
	void validate(ErrorRecorder er, Classification cl) {
		String classification_scheme = cl.getClassificationScheme();

		if (classification_scheme == null) {
			String classification_node = cl.getClassificationNode();
			if (classification_node == null || classification_node.equals("")) {
				cannotValidate(er, cl);
				return ;
			} else
				return;
		}
		if (classification_scheme.equals(MetadataSupport.XDSSubmissionSet_author_uuid))
			return;
		if (classification_scheme.equals(MetadataSupport.XDSDocumentEntry_author_uuid))
			return;
		String code = cl.getCodeValue();
		String coding_scheme = cl.getCodeScheme();

		if (code == null) {
			cannotValidate(er, cl);
			return ;
		}
		if (coding_scheme == null) {
			cannotValidate(er, cl);
			return;
		}
		for (OMElement code_type : MetadataSupport.childrenWithLocalName(codes, "CodeType")) {
			String class_scheme = code_type.getAttributeValue(MetadataSupport.classscheme_qname);

			// some codes don't have classScheme in their definition
			if (class_scheme != null && !class_scheme.equals(classification_scheme))
				continue;

			for (OMElement code_ele : MetadataSupport.childrenWithLocalName(code_type, "Code")) {
				String code_name = code_ele.getAttributeValue(MetadataSupport.code_qname);
				String code_scheme = code_ele.getAttributeValue(MetadataSupport.codingscheme_qname);
				if ( 	code_name.equals(code) && 
						(code_scheme == null || code_scheme.equals(coding_scheme) )
				) {
					return;
				}
			}
		}
		OMElement ele = cl.getElement();
		OMElement owner = null;
		if (ele != null) {
			OMContainer container = ele.getParent();
			if (container instanceof OMElement)
				owner = (OMElement) container;
		}
		if (vc.isXDM || vc.isXDR) 
			er.detail(objectDescription(owner) + ": the code " + coding_scheme + "(" + code + ") is not found in the Affinity Domain configuration");
		else
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(owner) + ": the code " + coding_scheme + "(" + code + ") is not found in the Affinity Domain configuration", this, "ITI TF-3: 4.1.10");
	}




}
