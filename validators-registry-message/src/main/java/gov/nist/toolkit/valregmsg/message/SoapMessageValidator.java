package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.soap.wsseToolkitAdapter.WsseHeaderValidatorAdapter;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.util.XMLUtils;
import org.w3c.dom.Element;

/**
 * Validate a SOAP wrapper according to ITI Appendix V and launch new
 * validator for contents of SOAP Body.
 * @author bill
 *
 */
public class SoapMessageValidator extends MessageValidator {
	OMElement envelope;
	OMElement header;
	OMElement body;
	OMElement messagebody = null;
	String wsaction = null;
	String reqMessageId = null; 
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	RegistryValidationInterface rvi;

	public String getWsAction() {
		return wsaction;
	}

	public String getMessageId() {
		return reqMessageId;
	}

	void err(String msg, String ref) {
		er.err(XdsErrorCode.Code.NoCode, msg, this, ref);
	}

	void err(String msg) {
		er.err(XdsErrorCode.Code.NoCode, msg, this, null);
	}

	void err(Exception e) {
		er.err(XdsErrorCode.Code.NoCode, e);
	}



	public SoapMessageValidator(ValidationContext vc, OMElement xml, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		this.envelope = xml;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.rvi = rvi;
	}
	
	// needed for junit testing
	public SoapMessageValidator(OMElement messagebody) {
		super(new ValidationContext());
		this.messagebody = messagebody;
	}

	public OMElement getMessageBody() {
		return messagebody;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;


		parse();
		envelope();
		header();
		parseWSAddressing();

		if (wsaction != null) {
			er.challenge("WS-Addressing Action Header");
			er.detail("Found WS-Action: " + wsaction);
			if (!wsaction.equals(wsaction.trim()))
				err("WS-Action contains whitespace prefix or suffix","");
		}

		messagebody = body();

		if (vc.isMessageTypeKnown()) {
			er.challenge("Checking WS-Action against SOAP Body contents");
			verifywsActionCorrectForValidationContext(wsaction);
			er.detail("Scheduling validation of body based on requested message type");
			MessageValidatorFactory.validateBasedOnValidationContext(erBuilder, messagebody, mvc, vc, rvi);
		} else if (messagebody == null) {
		} else {
			er.detail("Scheduling validation of body based on WS-Action");
			setValidationContextFromWSAction(vc, wsaction);
			if (vc.isValid()) {
				er.detail("Validation Context is " + vc);
				MessageValidatorFactory.validateBasedOnValidationContext(erBuilder, messagebody, mvc, vc, rvi);
			} else {
				err("Cannot validate SOAP Body - WS-Addressing Action header " + wsaction + " is not understood","ITI TF-2a, 2b, XDR, XCA, MPQ Supplements");
			}
		}
		
		//ADD SAML VALIDATION IF NEEDED. -@Antoine
		//TODO check if this is the best place to do so.
		OMElement security = MetadataSupport.firstChildWithLocalName(header, "Security");
		if(security != null){
			vc.hasSaml = true; // setting the flag is not really necessary, for consistency only.
			// mvc.addMessageValidator("SAML Validator", new SAMLMessageValidator(vc, envelope, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
			Element wsseHeader;
			try {
				wsseHeader = XMLUtils.toDOM(security);
				mvc.addMessageValidator("SAML Validator", new WsseHeaderValidatorAdapter(vc, wsseHeader,header), erBuilder.buildNewErrorRecorder());
			} catch (Exception e) {
				er.err(XdsErrorCode.Code.NoCode, e);
			}
		}
		
	}

	void parse() {
		header = MetadataSupport.firstChildWithLocalName(envelope, "Header");
		body = MetadataSupport.firstChildWithLocalName(envelope, "Body");
	}

	void verifywsActionCorrectForValidationContext(String wsaction) {
		ValidationContext v = new ValidationContext();
		
		v.clone(vc);
		
		setValidationContextFromWSAction(v, wsaction);
		if (!v.equals(vc)) {
			err("WS-Action wrong: " + wsaction + " not appropriate for message " + 
					vc.getTransactionName() + " required Validation Context is " + vc.toString() + 
					" Validation Context from WS-Action is " + v.toString(),"ITI TF");
		}
	}

	// is this fails to make a setting, it can be detected by the method
	// vc.isValid()
	void setValidationContextFromWSAction(ValidationContext vc, String wsaction) {
		if (wsaction == null)
			return;
		if (wsaction.equals("urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b")) {
			vc.isRequest = true;
			vc.isPnR = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse")) {
			vc.isResponse = true;
			vc.isPnR = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:RetrieveDocumentSet")) {
			vc.isRequest = true;
			vc.isRet = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:RetrieveDocumentSetResponse")) {
			vc.isResponse = true;
			vc.isRet = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:RegisterDocumentSet-b")) {
			vc.isRequest = true;
			vc.isR = true;
		} else if (wsaction.equals("urn:ihe:iti:2010:UpdateDocumentSet")) {
			vc.isRequest = true;
			vc.isMU = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:RegisterDocumentSet-bResponse")) {
			vc.isResponse = true;
			vc.isR = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:RegistryStoredQuery")) {
			vc.isRequest = true;
			vc.isSQ = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:RegistryStoredQueryResponse")) {
			vc.isResponse = true;
			vc.isSQ = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayQuery")) {
			vc.isRequest = true;
			vc.isSQ = true;
			vc.isXC = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayQueryAsync")) {
			vc.isRequest = true;
			vc.isSQ = true;
			vc.isXC = true;
			vc.isAsync = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayQueryResponse")) {
			vc.isResponse = true;
			vc.isSQ = true;
			vc.isXC = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayQueryAsyncResponse")) {
			vc.isResponse = true;
			vc.isSQ = true;
			vc.isXC = true;
			vc.isAsync = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayRetrieveAsyncResponse")) {
			vc.isResponse = true;
			vc.isRet = true;
			vc.isXC = true;
			vc.isAsync = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayRetrieveAsync")) {
			vc.isRequest = true;
			vc.isRet = true;
			vc.isXC = true;
			vc.isAsync = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayRetrieve")) {
			vc.isRequest = true;
			vc.isRet = true;
			vc.isXC = true;
		} else if (wsaction.equals("urn:ihe:iti:2007:CrossGatewayRetrieveResponse")) {
			vc.isResponse = true;
			vc.isRet = true;
			vc.isXC = true;
		} else if (wsaction.equals("urn:ihe:iti:2009:MultiPatientStoredQuery")) {
			vc.isRequest = true;
			vc.isSQ = true;
			vc.isMultiPatient = true;
		} else if (wsaction.equals("urn:ihe:iti:2009:MultiPatientStoredQueryResponse")) {
			vc.isResponse = true;
			vc.isSQ = true;
			vc.isMultiPatient = true;
		} else if (wsaction.equals("urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery")) {
			vc.isRequest = true;
            vc.isXcpd = true;
		} else if (wsaction.equals("urn:hl7-org:v3:PRPA_IN201306UV02:CrossGatewayPatientDiscovery")) {
			vc.isResponse = true;
            vc.isXcpd = true;
		} else if (wsaction.equals(SoapActionFactory.epsos_xcqr_action)) {
			vc.isRequest = true;
			vc.isSQ = true;
			vc.isXC = true;
			vc.isEpsos = true;
		} 

	}

	static String wsaddresingNamespace = "http://www.w3.org/2005/08/addressing";
	static String wsaddressingRef = "http://www.w3.org/TR/ws-addr-core/";

	void parseWSAddressing() {
		if (header == null)
			return;
		er.challenge("WS-Addressing");
		List<OMElement> messageId = MetadataSupport.childrenWithLocalName(header, "MessageID");
		List<OMElement> relatesTo = MetadataSupport.childrenWithLocalName(header, "RelatesTo");
		List<OMElement> to = MetadataSupport.childrenWithLocalName(header, "To");
		List<OMElement> action = MetadataSupport.childrenWithLocalName(header, "Action");
		List<OMElement> from = MetadataSupport.childrenWithLocalName(header, "From");
		List<OMElement> replyTo = MetadataSupport.childrenWithLocalName(header, "ReplyTo");
		List<OMElement> faultTo = MetadataSupport.childrenWithLocalName(header, "FaultTo");

		// check for namespace
		validateNamespace(messageId, wsaddresingNamespace);
		validateNamespace(relatesTo, wsaddresingNamespace);
		validateNamespace(to,        wsaddresingNamespace);
		validateNamespace(action,    wsaddresingNamespace);
		validateNamespace(from,      wsaddresingNamespace);
		validateNamespace(replyTo,   wsaddresingNamespace);
		validateNamespace(faultTo,   wsaddresingNamespace);

		// check for repeating and required elements
		// this does not take async into consideration
		if (to.size() > 1)
			err("Multiple WS-Addressing To headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (from.size() > 1)
			err("Multiple WS-Addressing From headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (replyTo.size() > 1)
			err("Multiple WS-Addressing ReplyTo headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (faultTo.size() > 1)
			err("Multiple WS-Addressing FaultTo headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (action.size() == 0)
			err("WS-Addressing Action header is required",wsaddressingRef + "#msgaddrpropsinfoset");
		if (action.size() > 1)
			err("Multiple WS-Addressing Action headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");
		if (messageId.size() > 	1) 
			err("Multiple WS-Addressing MessageID headers are not allowed",wsaddressingRef + "#msgaddrpropsinfoset");

		List<OMElement> hdrs = new ArrayList<OMElement> ();
		hdrs.addAll(messageId);
		hdrs.addAll(relatesTo);
		hdrs.addAll(to);
		hdrs.addAll(action);
		hdrs.addAll(from);
		hdrs.addAll(replyTo);
		hdrs.addAll(faultTo);

		boolean mufound = false;
		for (OMElement hdr : hdrs) {
			String mu = hdr.getAttributeValue(MetadataSupport.must_understand_qname);
			if (mu == null)
				continue;
			mufound = true;
			if (mustUnderstandValueOk(mu)) {
				mufound = true;
				break;
			}
//			er.detail("The WS-Addressing SOAP header " + hdr.getLocalName() + " has value other than \"1\""/*,"ITI TF-2x: V.3.2.2"*/);
		}
		if (!mufound) {
			err("At least one WS-Addressing SOAP header element must have a soapenv:mustUnderstand attribute soapenv:mustUnderstand with value of logical true","http://www.w3.org/TR/soap12-part0/#L4697");
			er.detail("Taken from the above reference:");
			er.detail("In the SOAP 1.2 infoset-based description, the env:mustUnderstand attribute in header elements takes the (logical) value \"true\" or \"false\", whereas in SOAP 1.1 they are the literal value \"1\" or \"0\" respectively.");
			er.detail("This validation accepts 1 or 0 or any capitalization of true or false");
		}

		//		if (action.size() > 0) {
		//			OMElement a = action.get(0);
		//			String mu = a.getAttributeValue(MetadataSupport.must_understand_qname);
		//			if (!"1".equals(mu))
		//				er.err("The WS-Action SOAP header element must have attribute wsa:mustUnderstand=\"1\"","ITI TF-2x: V.3.2.2");
		//		}

		// check for endpoint format
		endpointCheck(replyTo, false);
		endpointCheck(from, true);
		endpointCheck(faultTo, false);

		// check for simple http style endpoint
		httpCheck(to);

		// return WSAction

		wsaction = null;

		if (action.size() > 0) {
			OMElement aEle = action.get(0);
			wsaction = aEle.getText();
		}

		if (messageId.size() > 0) {
			OMElement mid = messageId.get(0);
			reqMessageId = mid.getText();
		}

	}
	
	boolean mustUnderstandValueOk(String value) {
		if ("1".equals(value)) return true;
		if ("true".equalsIgnoreCase("true")) return true;
		return false;
	}

	void httpCheck(List<OMElement> eles) {
		for (OMElement ele : eles) {
			String value = ele.getText();
			if (!value.startsWith("http"))
				err("Value of " + ele.getLocalName() + " must be http endpoint - found instead " + value,
						wsaddressingRef);
		}
	}

	void endpointCheck(List<OMElement> eles, boolean anyURIOk) {
		for (OMElement ele : eles) {
			OMElement first = ele.getFirstElement();
			if (first == null) {
				err("Validating contents of " + ele.getLocalName() + ": " + "not HTTP style endpoint"
						,wsaddressingRef);
				
			} else {
				String valError = validateEndpoint(first, anyURIOk);
				if (valError != null)
					err("Validating contents of " + ele.getLocalName() + ": " + valError
							,wsaddressingRef);
			}
		}
	}

	String validateEndpoint(OMElement endpoint, boolean anyURIOk) {
		if (endpoint == null)
			return "null value";
		if (!endpoint.getLocalName().equals("Address"))
			return "found " + endpoint.getLocalName() + " but expected Address";
		if (!endpoint.getNamespace().getNamespaceURI().equals(wsaddresingNamespace))
			return "found namespace" + endpoint.getNamespace().getNamespaceURI() + " but expected " + wsaddresingNamespace;
		String value = endpoint.getText();
		
		if (anyURIOk && value.startsWith("urn:"))
			return null;
		
		if (!value.startsWith("http"))
			return "not HTTP style endpoint";
		return null;
	}


	void validateNamespace(List<OMElement> eles, String namespace) {
		for (OMElement ele : eles) {
			OMNamespace omns = ele.getNamespace();
			String nsuri = omns.getNamespaceURI();
			if (!namespace.equals(nsuri))
				err("Namespace on element " + ele.getLocalName() + " must be " +
						namespace + " - found instead " + nsuri, 
						wsaddressingRef);
		}
	}

	static String soapEnvelopeNamespace = "http://www.w3.org/2003/05/soap-envelope";

	void header() {
		er.challenge("Header");
		if (header == null) {
			err("Header must be present","ITI TF-2x: V.3.2.2 and SOAP Version 1.2 Section 4");
			return;
		}
		OMNamespace ns = header.getNamespace();
		if (ns == null) 
			err("Namespace must be " + soapEnvelopeNamespace + " - found instead - " 
					+ "null","SOAP Version 1.2 Section 4");
		else {
			String uri = ns.getNamespaceURI();
			if (!soapEnvelopeNamespace.equals(uri))
				err("Namespace must be " + " - found instead - " 
						+ uri,"SOAP Version 1.2 Section 4");
		}
		
		OMElement metadataLevel = MetadataSupport.firstChildWithLocalName(header, "metadata-level");
		if (metadataLevel != null) {
			if (!"urn:direct:addressing".equals(metadataLevel.getNamespace().getNamespaceURI())) {
				err("Namespace on metadata-level header element must be " + "urn:direct:addressing");
			}
			String metadataLevelTxt = metadataLevel.getText();
			if (metadataLevelTxt == null) metadataLevelTxt = "";
			if (metadataLevelTxt.equals("minimal")) {
				vc.isXDRMinimal = true;
			} else if (metadataLevelTxt.equals("XDS")) {
				vc.isXDRMinimal = false;
			} else
				err("SOAP header metadata-level found but has invalid value = " + metadataLevelTxt + " ; must be minimal or XDS");
		}
	}

	OMElement body() {
		er.challenge("Body");
		if (body == null) {
			err("Body must be present","ITI TF-2x: V.3.2 and SOAP Version 1.2 Section 4");
			return null;
		} 

		if (header != null) {
			OMNamespace ns = header.getNamespace();
			if (ns == null) 
				err("Namespace must be " + soapEnvelopeNamespace + " - found instead - " 
						+ "null","http://www.w3.org/TR/soap12-part1/#soapenvelope");
			else {
				String uri = ns.getNamespaceURI();
				if (!soapEnvelopeNamespace.equals(uri))
					err("Namespace must be " + soapEnvelopeNamespace + " - found instead - " 
							+ uri,"http://www.w3.org/TR/soap12-part1/#soapenvelope");
			}
		} 

		List<String> kids = MetadataSupport.childrenLocalNames(body);
		if (kids.size() == 0) {
			err("Body must has a single child, found none", "ebRS 3.0 Section 2.1");
			return null;
		} else if (kids.size() == 1) {
			;
		} else {
			err("Body must have a single child, found " + kids.size(), "ebRS 3.0 Section 2.1");
		}
		return body.getFirstElement();
	}

	void envelope() {
		er.challenge("Envelope");
		OMNamespace ns = envelope.getNamespace();
		if (ns == null) 
			err("Envelope namespace must be " + soapEnvelopeNamespace + " - found instead - " 
					+ "null","ITI TF-2x: V.3.2.1.3 and http://www.w3.org/TR/soap12-part1/#soapenvelope");
		else {
			String uri = ns.getNamespaceURI();
			if (!soapEnvelopeNamespace.equals(uri))
				err("Envelope namespace must be " + soapEnvelopeNamespace + " - found instead - " 
						+ uri,"http://www.w3.org/TR/soap12-part1/#soapenvelope");
		}

		String eleName = envelope.getLocalName();
		if (eleName.equals("Envelope"))
			;
		else
			err("Envelope Element name must be Envelope - found instead - " 
					+ eleName,"http://www.w3.org/TR/soap12-part1/#soapenvelope");
	}

}
