package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testengine.Linkage;
import gov.nist.toolkit.testengine.RetContext;
import gov.nist.toolkit.testengine.RetInfo;
import gov.nist.toolkit.testengine.RetrieveB;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.io.Sha1Bean;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import gov.nist.toolkit.xdsexception.XdsPreparsedException;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

public class RetrieveTransaction extends BasicTransaction {
	String metadata_filename = null;
//	OMElement request_ele = null;
	OMElement expected_contents = null;   // never actually used - should remove
	String expected_mime_type = null;
	String uri = null;
	OMElement uri_ref = null;
	HashMap<String, String> referenced_documents = new HashMap<String, String>();  // uid, filename
	Metadata reference_metadata = null;
	boolean is_xca = false;
	boolean useIG = false;
	boolean removeHomeFromRequest = false;
	boolean clean_params = false;
	static Logger logger = Logger.getLogger(RetrieveTransaction.class);
	public String toString() {

		return "RetrieveTransaction: *************" +
		"\nmetadata_filename = " + metadata_filename +
		"\nexpected_contents = " + isNull(expected_contents) +
		"\nexpected_mime_type = " + expected_mime_type +
		"\nuri = " + uri +
		"\nuri_ref = " + isNull(uri_ref) +
		"\nreferenced_documents = " + referenced_documents.toString() +
		"\nreference_metadata = " + metadataStructure(reference_metadata) +
		"\nuse_document_unique_id = " + use_repository_unique_id.toString() +
		"\nuse_id = " + use_id +
		"\nuse_xpath = " + use_xpath +
		"\nlinkage = " + local_linkage_data.toString() +
		"\nendpoint = " + endpoint +
		"\nis_xca = " + is_xca +
		"\nactor config = " + testConfig.site +
		"\n****************";
	}

	String metadataStructure(Metadata m) {
		try {
			return m.structure();
		} catch (Exception e) { }
		return null;
	}

	String isNull(Object thing) { return (thing == null) ? "null" : "not null"; }

	public void setIsXca(boolean isXca) { is_xca = isXca; xds_version = BasicTransaction.xds_b; }

	public void setUseIG(boolean useIG) { this.useIG = useIG; }

	public RetrieveTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		defaultEndpointProcessing = false;
		parse_metadata = false;
		noMetadataProcessing = true;
	}

	public void run(OMElement request_ele)
	throws Exception {

		validate_xds_version();

		if (xds_version == BasicTransaction.xds_a) {
			throw new Exception("XDS.a no longer supported");
//			retrieve_a(s_ctx, instruction_output);
		} else {

			// metadata should be RequestDocumentSetRequest
//			OMElement metadata_ele = null;
//			if (metadata != null)
//				metadata_ele = metadata;
//			else
//				metadata_ele = Util.parse_xml(new File(metadata_filename));

//			if (metadata_filename == null && metadata_element == null)
//				throw new XdsInternalException("No MetadataFile element or Metadata element found for RetrieveDocumentSetRequest Transaction instruction within step " + s_ctx.get("step_id"));
//
//			if (metadata_filename != null)
//				request_ele = Util.parse_xml(new File(metadata_filename));

			// this looks useless, metadata here is the Retrive request
//			Metadata m = MetadataParser.noParse(metadata_ele);


//			if (use_id.size() > 0)
//				compileUseIdLinkage(m, use_id);
//
//			if (use_xpath.size() > 0)
//				compileUseXPathLinkage(m, use_xpath);
//
//			if (use_repository_unique_id.size() > 0)
//				compileUseRepositoryUniqueId(m, use_repository_unique_id);

			applyLinkage(request_ele);

			reportManagerPreRun(request_ele);

			if (repositoryUniqueId == null) {
				// if managed by ReportManager then need to extract it from request
				try {
					AXIOMXPath xpathExpression = new AXIOMXPath ("//*[local-name()='RepositoryUniqueId']");
					repositoryUniqueId = xpathExpression.stringValueOf(request_ele);
				} catch (Exception e) {
					fatal(e.getMessage());
				}
			}

			if (s_ctx.getPlan().getExtraLinkage() != null)
				testLog.add_name_value(instruction_output, "TemplateParams", s_ctx.getPlan().getExtraLinkage());

			if (clean_params)
				cleanRetParams(request_ele);

			if (is_xca) {
				String homeXPath = "//*[local-name()='RetrieveDocumentSetRequest']/*[local-name()='DocumentRequest'][1]/*[local-name()='HomeCommunityId']";
				String home = null;
				try {
					AXIOMXPath xpathExpression = new AXIOMXPath (homeXPath);
					home = xpathExpression.stringValueOf(request_ele);
				} catch (JaxenException e) {
					fatal ("XGR: " + ExceptionUtil.exception_details(e));
				}

				testLog.add_name_value(instruction_output, "InputMetadata", Util.deep_copy(request_ele));

				testLog.add_name_value(instruction_output, "Linkage", this.local_linkage_data.toString());

				if (this.useIG)
					parseIGREndpoint(home, testConfig.secure);
				else
					parseGatewayEndpoint(home, testConfig.secure);

				if (removeHomeFromRequest) {
					try {
						AXIOMXPath xpathExpression = new AXIOMXPath (homeXPath);
						List<?> nodes = xpathExpression.selectNodes(request_ele);
						for (OMElement node : (List<OMElement>) nodes) {
							node.detach();
						}
					} catch (JaxenException e) {
						fatal ("XGR: " + ExceptionUtil.exception_details(e));
					}


				}



			} else {
				// The above 'compile' steps may have updated critical sections of the metadata.  repositoryUniqueId is critical here.
				if (repositoryUniqueId == null || repositoryUniqueId.equals("")) {
					String xpath = "//*[local-name()='RetrieveDocumentSetRequest']/*[local-name()='DocumentRequest']/*[local-name()='RepositoryUniqueId']/text()";
					try {
						AXIOMXPath xpathExpression = new AXIOMXPath (xpath);
						String result = xpathExpression.stringValueOf(request_ele);
						repositoryUniqueId = result.trim();
					} catch (JaxenException e) {
						fatal("RetrieveTransaction: run(): XPATH error extracting repositoryUniqueId");
					}

				}

				testLog.add_name_value(instruction_output, "InputMetadata", Util.deep_copy(request_ele));

				testLog.add_name_value(instruction_output, "Linkage", this.local_linkage_data.toString());

				if (useReportManager == null &&
						(repositoryUniqueId == null || repositoryUniqueId.equals(""))) {
					fatal("RetrieveTransaction: no repositoryUniqueId");
				}

				// assign endpoint
				parseRepEndpoint(repositoryUniqueId, testConfig.secure);

				//System.out.println(this);
			}

			RetContext r_ctx = null;
			try {
				// map from doc uid -> info about doc
				// RetInfo holds size, hash, home etc
				HashMap<String, RetInfo> request_info = build_request_info(request_ele /* retrieve request */);

				// Bean that holds the context of the retrieve operation
				r_ctx = new RetContext();
				r_ctx.setRequestInfo(request_info);
				r_ctx.setRequest(request_ele);
				r_ctx.setExpectedError(s_ctx.getExpectedErrorMessage());

				RetrieveB ret_b = new RetrieveB(this, r_ctx, endpoint);
				ret_b.setUseReportManager(useReportManager);
				ret_b.setAsync(async);
				ret_b.setUseIG(useIG);
				ret_b.setExpectedMimeType(this.expected_mime_type);
				ret_b.setStepContext(instruction_output);
				ret_b.setIsXca(is_xca);
				ret_b.setSoap12(soap_1_2);
				ret_b.setReferenceMetadata(reference_metadata);
				OMElement result = ret_b.run();
				testLog.add_name_value(instruction_output, "Result", result);
				ret_b.validate();
			}
			catch (XdsPreparsedException e) {
				throw new XdsInternalException("Retrieve Error: endpoint was: " + endpoint + " " + e.getMessage(), e);
			}
			catch (Exception e) {
				throw new XdsInternalException("Retrieve Error: endpoint was: " + endpoint + " " + e.getMessage(), e);
			}

			add_step_status_to_output();

			// check that status == success
			String status = r_ctx.getRrp().get_registry_response_status();
			eval_expected_status(status, r_ctx.getRrp().get_error_code_contexts());

			String expErrorCode = s_ctx.getExpectedErrorCode();
			if (expErrorCode != null && !expErrorCode.equals("")) {
				List<String> errCodesReturned = r_ctx.getRrp().get_error_codes();
				if ( !errCodesReturned.contains(expErrorCode)) {
					s_ctx.set_error("Expected errorCode of " + expErrorCode + "\nDid get errorCodes of " +
							errCodesReturned);
					step_failure = true;
				}
			}
			reportManagerPostRun();
		}
	}

	@SuppressWarnings("unchecked")
	void cleanRetParams(OMElement ele) {
		AXIOMXPath xpathExpression;
		try {
			xpathExpression = new AXIOMXPath ("//*[local-name() = 'DocumentRequest']");
			List<OMElement> documentRequests = (List<OMElement>) xpathExpression.selectNodes(ele);
			if (documentRequests == null)
				return;

			for (OMElement docReq : documentRequests) {
				if (containsVariable(docReq))
					docReq.detach();
			}

		} catch (JaxenException e) {
		}
	}

	@SuppressWarnings("unchecked")
	boolean containsVariable(OMElement ele) {
		String valueStr = ele.getText();
		if (containsVariable(valueStr)) return true;
		for (Iterator<OMElement> it=(Iterator<OMElement>)ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (containsVariable(child))
				return true;
		}
		return false;
	}

	boolean containsVariable(String str) {
		if (str == null) return false;
		int i = str.indexOf("$");
		if (i == -1)
			return false;
		return true;
	}

	void update_referenced_documents() {
		HashMap<String, String> new_entries = new HashMap<String, String>();
		for (String ref_id : referenced_documents.keySet()) {
			if (local_linkage_data.containsKey(ref_id)) {
				new_entries.put(local_linkage_data.get(ref_id), referenced_documents.get(ref_id));
			}
		}
		referenced_documents.putAll(new_entries);
	}

	HashMap<String, String> parse_rep_request(OMElement rdsr) {
		HashMap<String, String> map = new HashMap<String, String>();  // docuid -> repuid

		for (OMElement document_request : XmlUtil.childrenWithLocalName(rdsr, "DocumentRequest")) {
			OMElement doc_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "DocumentUniqueId");
			String doc_uid = doc_uid_ele.getText();

			OMElement rep_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "RepositoryUniqueId") ;
			String rep_uid = rep_uid_ele.getText();
			map.put(doc_uid, rep_uid);
		}
		return map;
	}





	private HashMap<String, RetInfo> build_request_info(OMElement metadata_ele) throws XdsException {
		HashMap<String, RetInfo> request;
		request = new HashMap<String, RetInfo>();
		for (OMElement document_request : XmlUtil.childrenWithLocalName(metadata_ele, "DocumentRequest")) {
			//			request_list.add(document_request);

			OMElement doc_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "DocumentUniqueId");
			String doc_uid = doc_uid_ele.getText();

			OMElement rep_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "RepositoryUniqueId") ;
			String rep_uid = rep_uid_ele.getText();

			RetInfo rqst = new RetInfo();
			rqst.setDoc_uid(doc_uid);
			rqst.setRep_uid(rep_uid);

			//			if (reference_metadata != null) {
			//			HashMap<String, OMElement> uid_doc_map = reference_metadata.getDocumentUidMap();
			//			OMElement eo = uid_doc_map.get(doc_uid);
			//			if (eo == null)
			//			throw new XdsInternalException("RetrieveTransaction: build_request_info: reference document " + doc_uid + " not available");
			//			rqst.setHash(reference_metadata.getSlotValue(eo, "hash", 0));
			//			rqst.setSize(reference_metadata.getSlotValue(eo, "size", 0));
			//			rqst.setHome(reference_metadata.getHome(eo));
			//			}

			request.put(doc_uid, rqst);

			// linkage contains symbolic_name => real_name mapping
			// referenced_documents are keyed off symbolic_name
			// add real_name keys to referenced_documents
			for (Iterator<String> it=local_linkage_data.keySet().iterator(); it.hasNext(); ) {
				String symbolic_name = it.next();
				String real_name = local_linkage_data.get(symbolic_name);
				if (referenced_documents.containsKey(symbolic_name)) {
					referenced_documents.put(real_name, referenced_documents.get(symbolic_name));
				}
			}

			if (referenced_documents.containsKey(doc_uid)) {
				String filename = referenced_documents.get(doc_uid);

				try {
					FileInputStream fis = new FileInputStream(new File(filename));
					rqst.setContents(Io.getBytesFromInputStream(fis));
				} catch (Exception e) {
					throw new XdsInternalException("Cannot read ReferenceDocument: " + filename);
				}
			}
			//			else {
			//				throw new XdsInternalException("referenced_documents does not contain " + doc_uid +
			//						"\nit has only " + referenced_documents +
			//						"\nand linkage contains " + linkage);
			//			}
		}
		return request;
	}

//	private void retrieve_a(StepContext s_ctx, OMElement instruction_output)
//	throws XdsInternalException, FactoryConfigurationError,
//	MetadataException, MetadataValidationException, XdsException {
//		if ( uri == null && uri_ref == null)
//			throw new XdsInternalException("No URI or URIRef element within step " + s_ctx.get("step_id"));
//
//		s_ctx.add_name_value(instruction_output, "URIRef", uri_ref);
//
//		if (uri_ref != null) {
//			DocDetails docDetails = getDocDetailsFromLogfile(uri_ref);
//
//			if (docDetails.uri == null || docDetails.uri.equals("")) {
//				fail ("URI not available from query results");
//				return;
//			}
//			if (docDetails.size == null || docDetails.size.equals("")) {
//				fail ("size not available from query results");
//				return;
//			}
//			if (docDetails.hash == null || docDetails.hash.equals("")) {
//				fail ("hash not available from query results");
//				return;
//			}
//			if (docDetails.mimeType == null || docDetails.mimeType.equals("")) {
//				fail ("mimeType not available from query results");
//				return;
//			}
//			s_ctx.add_name_value(instruction_output, "URI_from_query", docDetails.uri);
//			s_ctx.add_name_value(instruction_output, "size_from_query", docDetails.size);
//			s_ctx.add_name_value(instruction_output, "hash_from_query", docDetails.hash);
//			s_ctx.add_name_value(instruction_output, "mimeType_from_query", docDetails.mimeType);
//
//			RetrieveA reta = new RetrieveA(docDetails.uri);
//
//			byte[] contents = reta.retrieve();
//			String mimeType = reta.get_content_type();
//			if (reta.has_errors()) {
//				throw new XdsInternalException(reta.get_errors());
//			}
//
//			s_ctx.add_name_value(instruction_output, "mimeType", mimeType );
//
//			String size = String.valueOf(contents.length);
//			s_ctx.add_name_value(instruction_output, "size", size );
//
//			String hash = null;
//			try {
//				hash = sha1(contents);
//				s_ctx.add_name_value(instruction_output, "hash", hash );
//			}
//			catch (Exception e) {
//				throw new XdsInternalException(ExceptionUtil.exception_details(e, "Sha1 computation failed"));
//			}
//
//			if (!docDetails.size.equals(size))
//				fail("Size does not match, from query = " + docDetails.size + " and from retrieve = " + size);
//
//			if (!docDetails.hash.equals(hash))
//				fail("Hash does not match, from query = " + docDetails.hash + " and from retrieve = " + hash);
//
//			if (!docDetails.mimeType.equals(mimeType))
//				fail("mimeType does not match, from query = " + docDetails.mimeType + " and from retrieve = " + mimeType);
//
//
//		} else {   // uri used
//
//			RetrieveA reta = new RetrieveA(uri);
//
//			byte[] contents = reta.retrieve();
//			String content_type = reta.get_content_type();
//			if (reta.has_errors()) {
//				throw new XdsInternalException(reta.get_errors());
//			}
//
//			if (expected_mime_type != null && !expected_mime_type.equals(content_type))
//				fail("Expected mime type of " + expected_mime_type +
//						" but got " + content_type);
//
//			s_ctx.add_name_value(instruction_output, "mimetype", content_type);
//
//			s_ctx.add_name_value(instruction_output, "size", String.valueOf(contents.length));
//			String returned_doc_hash = null;
//			try {
//				returned_doc_hash = sha1(contents);
//				s_ctx.add_name_value(instruction_output, "hash", returned_doc_hash );
//			}
//			catch (Exception e) {
//				throw new XdsInternalException(ExceptionUtil.exception_details(e, "Sha1 computation failed"));
//
//			}
//
//			if ( !referenced_documents.isEmpty()) {
//				String key = referenced_documents.keySet().iterator().next();
//				String filename = referenced_documents.get(key);
//
//
//
//				try {
//					FileInputStream fis = new FileInputStream(new File(filename));
//					byte[] in_bytes = Io.getBytesFromInputStream(fis);
//					String reference_document_hash = sha1(in_bytes);
//
//					if ( !returned_doc_hash.equals(reference_document_hash))
//						fail("Hash does not match: submitted document has hash of " +
//								reference_document_hash +
//								" and returned document has hash of " +
//								returned_doc_hash);
//
//				}
//				catch (IOException e) {
//					fail("Cannot read ReferenceDocument: " + filename);
//				}
//				catch (Exception e) {
//					fail("sha1 calculation failed");
//				}
//			}
//
//		}
//	}

	protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
		String part_name = part.getLocalName();
		if (part_name.equals("MetadataFile")) {
			metadata_filename = testConfig.testplanDir + part.getText();
			testLog.add_name_value(instruction_output, "MetadataFile", metadata_filename);
		}
//		else if (part_name.equals("Metadata")) {
//			metadata_filename = "";
//			request_ele = part.getFirstElement();
//		}
		else if (part_name.equals("ExpectedContents")) {
			expected_contents = part;
			testLog.add_name_value(instruction_output, "ExpectedContents", part);
		}
		else if (part_name.equals("ExpectedMimeType")) {
			expected_mime_type = part.getText();
			testLog.add_name_value(instruction_output, "ExpectedMimeType", part);
		}
		else if (part_name.equals("RemoveHomeFromRequest")) {
			removeHomeFromRequest = true;
		}
		else if (part_name.equals("ReferenceDocument")) {
			String filename = null;
			String uid = null;
			filename =testConfig.testplanDir + File.separator + part.getText();
			uid = part.getAttributeValue(new QName("uid"));
			referenced_documents.put(uid, filename);
			testLog.add_name_value(instruction_output, "ReferenceDocument", part);
		}
		else if (part_name.equals("ReferenceMetadata")) {
			String testdir = part.getAttributeValue(new QName("testdir"));
			String step = part.getAttributeValue(new QName("step"));
			if (testdir == null || testdir.equals("") | step == null || step.equals(""))
				throw new XdsInternalException("ReferenceMetadata instruction: both testdir and step are required attributes");
			reference_metadata = new Linkage(testConfig).getResult(testdir, step);
		}
		else if (part_name.equals("UseId")) {
			use_id.add(part);
			testLog.add_name_value(instruction_output, "UseId", part);
		}
		else if (part_name.equals("UseRepositoryUniqueId")) {
			this.use_repository_unique_id.add(part);
			testLog.add_name_value(instruction_output, "UseRepositoryUniqueId", part);
		}
		else if (part_name.equals("UseXPath")) {
			use_xpath.add(part);
			testLog.add_name_value(instruction_output, "UseXRef", part);
		}
//		else if (part_name.equals("Assertions")) {
//			parse_assertion_instruction(part);
//		}
		else if (part_name.equals("XDSb")) {
			xds_version = BasicTransaction.xds_b;
		}
		else if (part_name.equals("SOAP11")) {
			soap_1_2 = false;
			testLog.add_simple_element(this.instruction_output, "SOAP11");
		}
		else if (part_name.equals("URI")) {
			uri = part.getText();
		}
		else if (part_name.equals("URIRef")) {
			uri_ref = part;
		}
		else if (part_name.equals("CleanParams")) {
			clean_params = true;
			testLog.add_name_value(instruction_output, "CleanParams", part);
		}
		else if (part_name.equals("XDSa")) {
			xds_version = BasicTransaction.xds_a;
			//throw new XdsException("Retrieve transaction (in xdstest2) does not support XDS.a");
		} else {
			//				throw new XdsException("Don't understand instruction " + part_name + " inside step " + s_ctx.getId());
			parseBasicInstruction(part);
		}
	}

	//	private String compute_hash(InputStream is)
	//	throws MetadataException, XdsIOException, XdsInternalException, XdsConfigurationException, XdsException {
	//	ByteBuffer buffer = new ByteBuffer();
	//	int length = 4000;
	//	byte[] buf = new byte[length];
	//	int size = 0;
	//	try { size = is.read(buf, 0, length); }  catch (IOException e) {   throw new XdsIOException("Error when starting to read document content");   }
	//	buffer.append(buf, 0, size);
	//	while (size > 0) {
	//	try { size = is.read(buf, 0, length); }  catch (IOException e) {   throw new XdsIOException("Error reading document content");  }
	//	buffer.append(buf,0, size);
	//	}
	//	try { is.close();  } catch (IOException e) {   throw new XdsIOException("Error closing repository item input stream");   }

	//	// set size, hash, URI into metadata
	//	return (new Hash()).compute_hash(buffer);
	//	}

	String sha1(byte[] buf) throws Exception {
		Sha1Bean sb = new Sha1Bean();
		sb.setByteStream(buf);
		return sb.getSha1String();
	}

	@Override
	protected String getRequestAction() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getBasicTransactionName() {
		return "ret";
	}



	//	private String validate_expected_contents(StepContext s_ctx, OMElement result, OMElement instruction_output, int metadata_type, OMElement expected_contents)
	//	throws XdsInternalException, MetadataException, MetadataValidationException {
	//	return "";
	//	}

}
