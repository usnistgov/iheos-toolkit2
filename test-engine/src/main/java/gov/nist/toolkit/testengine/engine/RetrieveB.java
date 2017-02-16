package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.RegistryResponseParser;
import gov.nist.toolkit.registrymsg.repository.RetrieveResponseParser;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testengine.transactions.BasicTransaction;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.xdsexception.SchemaValidationException;
import gov.nist.toolkit.xdsexception.XdsConfigurationException;
import gov.nist.toolkit.xdsexception.XdsIOException;
import gov.nist.toolkit.xdsexception.XdsPreparsedException;
import gov.nist.toolkit.xdsexception.XdsWSException;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.IOException;
import java.util.HashMap;

public class RetrieveB {
	String endpoint = null;
	protected RetContext r_ctx = null;
	protected OMElement log_parent = null;
	protected Metadata reference_metadata = null;
	private String expected_mime_type = null;
	boolean is_xca = false;
	boolean useIG = false;
	boolean soap12 = true;
	boolean async = false;
	UseReportManager useReportManager = null;
	ReportManager reportManager = null;
	BasicTransaction basicTransaction;
	OmLogger testLog = new TestLogFactory().getLogger();

	public void setIsXca(boolean isXca) { is_xca = isXca; }

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void setUseIG(boolean useIG) {
		this.useIG = useIG;
	}

	public RetrieveB(BasicTransaction basic) {
		basicTransaction = basic;
	}

	public RetrieveB() {
	}

	public RetrieveB(BasicTransaction basic, RetContext r_ctx, String endpoint) {
		basicTransaction = basic;
		this.r_ctx = r_ctx;
		this.endpoint = endpoint;
	}

	public void setUseReportManager(UseReportManager m ) {
		useReportManager = m;
	}

	public void setStepContext(OMElement log_parent) {
		this.log_parent = log_parent;
	}

	public void setExpectedMimeType(String type) {
		expected_mime_type = type;
	}

	public void setReferenceMetadata(Metadata reference_metadata) {
		this.reference_metadata = reference_metadata;
	}

	public void setSoap12(boolean v) { soap12 = v; }

	OMElement build_request(RetContext rc) {
		String ns = MetadataSupport.xdsB.getNamespaceURI();
		OMElement rdsr = MetadataSupport.om_factory.createOMElement(new QName(	ns, "RetrieveDocumentSetRequest"));
		for (String uid : rc.getRequestInfo().keySet()) {
			RetrievedDocumentModel ri = rc.getRequestInfo().get(uid);

			OMElement dr = MetadataSupport.om_factory.createOMElement(new QName(	ns, "DocumentRequest"));
			rdsr.addChild(dr);

			if (ri.getHome() != null && !ri.getHome().equals("")) {
				OMElement hci = MetadataSupport.om_factory.createOMElement(new QName(	ns, "HomeCommunityId"));
				hci.setText(ri.getHome());
				dr.addChild(hci);
			}

			OMElement ruid = MetadataSupport.om_factory.createOMElement(new QName(	ns, "RepositoryUniqueId"));
			ruid.setText(ri.getRepUid());
			dr.addChild(ruid);

			OMElement duid = MetadataSupport.om_factory.createOMElement(new QName(	ns, "DocumentUniqueId"));
			duid.setText(ri.getDocUid());
			dr.addChild(duid);
		}
		return rdsr;
	}

	public OMElement run()
	throws XdsInternalException, FactoryConfigurationError,
			XdsException, XdsIOException, MetadataException,
	XdsConfigurationException, MetadataValidationException, XdsWSException, AxisFault, EnvironmentNotSelectedException {

		OMElement result = null;
		OMElement request = r_ctx.getRequest();
		if (request == null)
			r_ctx.setRequest(build_request(r_ctx));


		result = call(r_ctx.getRequest(), endpoint); // AxisFault will signal the caller that the endpoint did not work
		r_ctx.setResult(result);

		return result;
	}

	public void validate() throws XdsInternalException, MetadataException {
		OMElement result = r_ctx.getResult();

		if (result == null)
			throw new XdsInternalException("Response is null");

		OMElement registry_response = XmlUtil.firstChildWithLocalName(result, "RegistryResponse") ;
		if (registry_response == null) {
			throw new XdsInternalException("Did not find RegistryResponse within RetrieveDocumentSetResponse");
		}

		// schema validate
		RegistryResponseParser rrp = new RegistryResponseParser(registry_response);
		r_ctx.setRrp(rrp);

		String errors = rrp.get_regrep_error_msg();

		if (r_ctx.getExpectedError() != null) {
			if (errors == null || errors.indexOf(r_ctx.getExpectedError()) == -1)
				throw new MetadataException("Expected error " + r_ctx.getExpectedError() + " not found", null);
		} else {

			//System.out.println("Retrieve errors: [" + errors + "]");
			if (errors != null && !errors.equals(""))
				throw new XdsPreparsedException("Error: " + errors);
		}

		try {
			RegistryUtility.schema_validate_local(result, MetadataTypes.METADATA_TYPE_RET);
		} catch (SchemaValidationException e) {
			throw new XdsInternalException("Schema validation of Retrieve Response: " + e.getMessage());
		}

		// validate response contents
		try {
			r_ctx.setResponseInfo(parse_rep_response(result));
			String validation_errors = validate_retrieve();
			if (validation_errors != null && !validation_errors.equals("") && r_ctx.getExpectedError() == null) {
				throw new XdsPreparsedException(validation_errors);
			}
		} catch (Exception e) {
			throw new XdsPreparsedException("Result validation threw exception: " + RegistryUtility.exception_details(e));
		}
	}

	private OMElement call(OMElement metadata_ele, String endpoint)
	throws XdsWSException, XdsException, AxisFault, EnvironmentNotSelectedException {
			Options options = new Options();
			options.setTo(new EndpointReference(endpoint)); // this sets the location of MyService service
			ServiceClient serviceClient = new ServiceClient();
			serviceClient.setOptions(options);
			OMElement result;
			Soap soap = new Soap();


		boolean samlEnabled = Installation.instance().propertyServiceManager().getPropertyManager().isEnableSaml();
		TransactionSettings transactionSettings = this.basicTransaction.getStepContext().getTransactionSettings();
		if(samlEnabled && transactionSettings!=null &&  transactionSettings.siteSpec != null && transactionSettings.siteSpec.isSaml){
			soap.setGazelleXuaUsername(transactionSettings.siteSpec.getGazelleXuaUsername());
			if (transactionSettings.siteSpec.getStsAssertion()!=null) {
				soap.addHeader(basicTransaction.getSecurityEl(transactionSettings.siteSpec.getStsAssertion()));
			}
		}
//		soap = testConfig.soap;
		soap.setAsync(async);

			// securityParams not used anymore??
			soap.setSecurityParams(this.basicTransaction.getStepContext().getTransactionSettings().securityParams);

			soap.setAsync(async);
			soap.soapCall(metadata_ele, endpoint,
					true,    // mtom
					true,     // addressing
					soap12,     // soap12
					getRequestAction(),
					getResponseAction());
			result = soap.getResult();

			basicTransaction.logSoapRequest(soap);


			return result;
	}

	protected String getResponseAction() {
		return SoapActionFactory.getResponseAction(getRequestAction());
	}

	protected String getRequestAction() {
		if (async) {
			if (is_xca && !useIG) {
				return "urn:ihe:iti:2007:CrossGatewayRetrieve";
			} else {
				return "urn:ihe:iti:2007:RetrieveDocumentSet";
			}
		} else {
			if (is_xca && !useIG) {
				return "urn:ihe:iti:2007:CrossGatewayRetrieve";
			} else {
				return "urn:ihe:iti:2007:RetrieveDocumentSet";
			}
		}
	}



	// map key is docUid
	public RetrievedDocumentsModel parse_rep_response(OMElement response) throws IOException, MetadataException, Exception {

        RetrievedDocumentsModel map = new RetrieveResponseParser(response).get();
        int i=0;
        for (RetrievedDocumentModel retrievedDocumentModel : map.values()) {
            if (useReportManager != null) {
                useReportManager.setRetInfo(retrievedDocumentModel, 1);
            }
            if (reportManager != null)
            	reportManager.setRetInfo(retrievedDocumentModel, i);
            i++;
        }
        return map;
	}

	protected String validate_retrieve() throws MetadataException {
        RetrievedDocumentsModel request = r_ctx.getRequestInfo();
        RetrievedDocumentsModel response = r_ctx.getResponseInfo();
		StringBuffer errors = new StringBuffer();
		HashMap<String, OMElement> uid_doc_map = null;   // UUID -> ExtrinsicObject

		if (reference_metadata != null)
			uid_doc_map = reference_metadata.getDocumentUidMap();

		if (request.size() != response.size())
			errors.append("Requested [" + request.size() + "] docs, got [" + response.size() + "]\n");

		for (String req_doc : request.keySet()) {
			//System.out.println("validating " + req_doc);
			RetrievedDocumentModel req = request.get(req_doc);
			RetrievedDocumentModel rsp = response.get(req_doc);

			String doc_uid = req.getDocUid();
			OMElement eo = null;
			if (uid_doc_map != null)
				eo = uid_doc_map.get(doc_uid);
			String query_size = null;
			String query_hash = null;
			String query_mime_type = null;
			if (uid_doc_map == null) {

			}
			else if (eo == null)
				errors.append("Retrieve validation: Document with uid = [" + doc_uid + "] not present in query output\n");
			else {
				query_size = this.reference_metadata.getSlotValue(eo, "size", 0);
				query_hash = this.reference_metadata.getSlotValue(eo, "hash", 0);
				query_mime_type = this.reference_metadata.getMimeType(eo);
			}

			if (query_mime_type == null)
				query_mime_type = this.expected_mime_type;

			if (rsp == null) {
				errors.append("No response for document <" + req_doc +
						"> - only have responses for documents <" + response.keySet() +
				">\n");
				continue;
			}
			//			errors.append("Request:\n" + req.toString() + "\n");
			//			errors.append("Response:\n" + rsp.toString() + "\n");

			if (req.getRepUid() == null) {
				errors.append("Request repositoryUniqueId is null\n");
				continue;
			}

			if ( !req.getRepUid().equals(rsp.getRepUid()))
				errors.append("Request repositoryUniqueId does not match response - [" + req.getRepUid() + "] vs [" + rsp.getRepUid() + "]\n");

			if (rsp.getContents() == null || req.getContents() == null) {
				boolean err = false;
//				if (req.getContents() == null) {
//					errors.append("Reference document not accessible\n");
//					err = true;
//				}
				if (rsp.getContents() == null) {
					errors.append("No document data\n");
					err = true;
				}
				if (err)
					continue;
			}

			if (this.log_parent != null) {
				testLog.add_name_value(
						this.log_parent,
						"ContentType",
						testLog.create_name_value("Original", req.getContent_type()),
						testLog.create_name_value("Query", query_mime_type),
						testLog.create_name_value("Retrieve", rsp.getContent_type()));
				testLog.add_name_value(
						this.log_parent,
						"Hash",
						testLog.create_name_value("Original", req.getHash()),
						testLog.create_name_value("Query", query_hash),
						testLog.create_name_value("Retrieve", rsp.getHash()));
				testLog.add_name_value(
						this.log_parent,
						"Size",
						testLog.create_name_value("Original", String.valueOf(req.getSize())),
						testLog.create_name_value("Query", query_size),
						testLog.create_name_value("Retrieve", String.valueOf(rsp.getSize())));
			}

			//
			// mime type
			//
			if (req.getContent_type() == null || req.getContent_type().equals("")) {
				// in some tests it isn't available
			}
			else if (rsp.getContent_type() == null) {
				errors.append("Null Content-Type - expected [" + req.getContent_type() + "]" );
			}
			else if ( !rsp.getContent_type().equals(req.getContent_type()))
				errors.append("Content type does not match - submission has [" + req.getContent_type() + "] and Retrieve response has [" + rsp.getContent_type() + "]\n");

			if (query_mime_type != null && !query_mime_type.equals(rsp.getContent_type()))
				errors.append("Content type from query response has [" + query_mime_type + "] and Retrieve response has [" + rsp.getContent_type() + "]\n");

			//
			// hash
			//
			if ( req.getHash() != null && rsp.getHash() != null && !rsp.getHash().equals(req.getHash().toLowerCase()))
				errors.append("Hash does not match - submission has [" + req.getHash() + "] and Retrieve response has [" + rsp.getHash() + "]\n");

			if (query_hash != null && !query_hash.equals(rsp.getHash()))
				errors.append("Hash does not match - query response has [" + query_hash + "] and Retrieve response has [" + rsp.getHash() + "]\n");

			//
			// size
			//
			if ( rsp.getSize() != req.getSize() && req.getSize() != -1)
				errors.append("Size does not match - metadata has [" + req.getSize() + "] and Retrieve response has [" + rsp.getSize() + "]\n");

			if (query_size != null) {
				int query_size_int = Integer.parseInt(query_size);
				if (query_size_int != rsp.getSize())
					errors.append("Size does not match - query response has [" + query_size_int + "] and Retreive response has [" + rsp.getSize() + "]\n");
			}

		}

		if (errors.length() == 0)
			return "";
		else
			return "\nRetrieve Validation Errors:\n" + errors.toString();

	}


	public void setReportManager(ReportManager reportManager) {
		this.reportManager = reportManager;
	}
}
