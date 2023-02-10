package gov.nist.toolkit.validatorsSoapMessage.message;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.validation.factories.ValidationContextValidationFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Validate a SOAP wrapper according to ITI Appendix V and launch new
 * validator for contents of SOAP Body.
 * @author bill
 *
 */
public class SoapMessageValidator extends AbstractMessageValidator {
    OMElement envelope;
    OMElement header;
    OMElement body;
    OMElement messagebody = null;
    String wsaction = null;
    String reqMessageId = null;
    ErrorRecorderBuilder erBuilder;
    MessageValidatorEngine mvc;
    RegistryValidationInterface rvi;
    TestSession testSession;

    public String getWsAction() {
        return wsaction;
    }

    public String getMessageId() {
        return reqMessageId;
    }

    public OMElement getEnvelope() {
        return envelope;
    }

    public OMElement getHeader() {
        return header;
    }

    public OMElement getBody() {
        return body;
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



    public SoapMessageValidator(ValidationContext vc, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi, TestSession testSession) {
        super(vc);
        this.erBuilder = erBuilder;
        this.mvc = mvc;
        this.rvi = rvi;
        this.testSession = testSession;
    }

    // needed for junit testing
    public SoapMessageValidator(OMElement messagebody) {
        super(DefaultValidationContextFactory.validationContext());
        this.messagebody = messagebody;
    }

    public OMElement getMessageBody() {
        return messagebody;
    }

    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        this.er = er;
        er.registerValidator(this);

        SoapMessageParser smp = (SoapMessageParser) mvc.findMessageValidator("SoapMessageParser");

        er.test(smp.getHeaders().size() == 1, "", "Single SOAP Header", Integer.toString(smp.getHeaders().size()), "1", "");
        er.test(smp.getBodies().size() == 1, "", "Single SOAP Body", Integer.toString(smp.getBodies().size()), "1", "");
        envelope = smp.getEnvelope();
        header = smp.getHeader();
        body = smp.getBody();

        validateEnvelope();
        validateHeader();

        messagebody = body();

        if (vc.isMessageTypeKnown()) {
            // Validation challenge has been established - verify against it
            er.challenge("Checking expected WS-Action against SOAP Body contents");
            verifywsActionCorrectForValidationContext(wsaction);
            er.detail("Scheduling validation of body based on requested message type");
            ValidationContextValidationFactory.validateBasedOnValidationContext(erBuilder, messagebody, mvc, vc, rvi);
        } else if (messagebody == null) {
            // Is this an error?
        } else {
            // Validation challenge is established by examining the WS:Action
            er.detail("Scheduling validation of body based on WS-Action");
            setValidationContextFromWSAction(vc, wsaction);
            if (vc.isValid()) {
                er.detail("Validation Context is " + vc);
                ValidationContextValidationFactory.validateBasedOnValidationContext(erBuilder, messagebody, mvc, vc, rvi);
            } else {
                err("Cannot validate SOAP Body - WS-Addressing Action header " + wsaction + " is not understood","ITI TF-2a, 2b, XDR, XCA, MPQ Supplements");
            }
        }



        //ADD SAML VALIDATION IF NEEDED. -@Antoine
        // - check if this is the best place to do so.
//        OMElement security = XmlUtil.firstChildWithLocalName(header, "Security");
//        if(security != null){
//            vc.hasSaml = true; // setting the flag is not really necessary, for consistency only.
//            // mvc.addMessageValidator("SAML Validator", new SAMLMessageValidator(vc, envelope, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
//            Element wsseHeader;
//            try {
//                wsseHeader = XMLUtils.toDOM(security);
//                mvc.addMessageValidator("SAML Validator", new WsseHeaderValidatorAdapter(vc, wsseHeader,header), erBuilder.buildNewErrorRecorder());
//            } catch (Exception e) {
//                er.err(XdsErrorCode.Code.NoCode, e);
//            }
//        }

        /*
            Gazelle STS SAML validation
            - Sunil
         */
        if (header!=null && vc.requiresStsSaml && vc.isRequest) {
            boolean samlEnabled = Installation.instance().propertyServiceManager().getPropertyManager().isEnableSaml();
            if (samlEnabled) {
                mvc.addMessageValidator("STS SAML Validator", new StsSamlValidator(vc, er, mvc, rvi, header, testSession), erBuilder.buildNewErrorRecorder());
            } else {
               er.detail("SAML bypassed.");
            }
        }

        er.unRegisterValidator(this);
    }


    void verifywsActionCorrectForValidationContext(String wsaction) {
        ValidationContext v = DefaultValidationContextFactory.validationContext();

        v.clone(vc);

        setValidationContextFromWSAction(v, wsaction);
        //  - finish this
        String expected = "???";
        if (!v.equals(vc)) {
            er.error("???", "Expected WS:Action", wsaction, expected, "???");
//            err("WS-Action wrong: " + wsaction + " not appropriate for message " +
//                    vc.getTransactionName() + " required Validation Context is " + vc.toString() +
//                    " Validation Context from WS-Action is " + v.toString(),"ITI TF");
        }
    }


    // wsaction ==> isRequest (true/false), isPnr/isRet/isR/isMU...
    // need reverse



    static public void setValidationContextFromWSAction(ValidationContext vc, String wsaction) {
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
        } else if (wsaction.equals("urn:ihe:iti:2018:RestrictedUpdateDocumentSet")) {
            vc.isRequest = true;
            vc.isRMU = true;
        } else if (wsaction.equals("urn:ihe:iti:2010:DeleteDocumentSet")) {
            vc.isRequest = true;
            vc.isRM = true;
        } else if (wsaction.equals("urn:ihe:iti:2017:RemoveDocuments")) {
            vc.isRequest = true;
            vc.isRD = true;
        } else if (wsaction.equals("urn:ihe:iti:2018:RegisterDocumentSet-bResponse")) {
            vc.isResponse = true;
            vc.isR = true;
        } else if (wsaction.equals("urn:ihe:iti:2010:RegisterOnDemandDocumentEntry")) {
            vc.isRequest = true;
            vc.isRODDE = true;
        } else if (wsaction.equals("urn:ihe:iti:2010:RegisterOnDemandDocumentResponse")) {
            vc.isResponse = true;
            vc.isRODDE = true;
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
        } else if (wsaction.equals("urn:ihe:iti:2015:CrossGatewayDocumentProvide")) {
            vc.isRequest = true;
            vc.isPnR = true;
            vc.isXC = true;
            vc.isXCDR = true;
        } else if (wsaction.equals("urn:ihe:iti:2015:CrossGatewayDocumentProvideResponse")) {
            vc.isResponse = true;
            vc.isPnR = true;
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
        } else if (wsaction.equals("urn:ihe:rad:2009:RetrieveImagingDocumentSet")) {
           vc.isRequest = true;
           vc.isRad69 = true;
        }
    }

    static String wsaddresingNamespace = "http://www.w3.org/2005/08/addressing";
    static String wsaddressingRef      = "http://www.w3.org/TR/ws-addr-core/";
    static String xdrNamespace         = "urn:ihe:iti:xdr:2014";

    void validateWSAddressing() {
        if (header == null)
            return;
        List<OMElement> messageId = XmlUtil.childrenWithLocalName(header, "MessageID");
        List<OMElement> relatesTo = XmlUtil.childrenWithLocalName(header, "RelatesTo");
        List<OMElement> to = XmlUtil.childrenWithLocalName(header, "To");
        List<OMElement> action = XmlUtil.childrenWithLocalName(header, "Action");
        List<OMElement> from = XmlUtil.childrenWithLocalName(header, "From");
        List<OMElement> replyTo = XmlUtil.childrenWithLocalName(header, "ReplyTo");
        List<OMElement> faultTo = XmlUtil.childrenWithLocalName(header, "FaultTo");

        // check for namespace
        int errCount = er.getNbErrors();
        validateNamespace(messageId, wsaddresingNamespace);
        validateNamespace(relatesTo, wsaddresingNamespace);
        validateNamespace(to,        wsaddresingNamespace);
        validateNamespace(action,    wsaddresingNamespace);
        validateNamespace(from,      wsaddresingNamespace);
        validateNamespace(replyTo,   wsaddresingNamespace);
        validateNamespace(faultTo,   wsaddresingNamespace);
        if (errCount == er.getNbErrors())
            er.report("Namespace on WS:Addressing Header elements", "No errors");

        // check for repeating and required elements
        // this does not take async into consideration
        errCount = er.getNbErrors();
        er.test(to.size() < 2, "", "To header cardinality", Integer.toString(to.size()), "0 or 1", wsaddressingRef + "#msgaddrpropsinfoset");
        er.test(from.size() < 2, "", "From header cardinality", Integer.toString(from.size()), "0 or 1", wsaddressingRef + "#msgaddrpropsinfoset");
        er.test(replyTo.size() < 2, "", "ReplyTo header cardinality", Integer.toString(replyTo.size()), "0 or 1", wsaddressingRef + "#msgaddrpropsinfoset");
        er.test(faultTo.size() < 2, "", "FaultTo header cardinality", Integer.toString(faultTo.size()), "0 or 1", wsaddressingRef + "#msgaddrpropsinfoset");
        er.test(action.size() == 1, "", "Action header cardinality", Integer.toString(action.size()), "1", wsaddressingRef + "#msgaddrpropsinfoset");
        er.test(messageId.size() < 2, "", "MessageID header cardinality", Integer.toString(messageId.size()), "0 or 1", wsaddressingRef + "#msgaddrpropsinfoset");
        if (errCount == er.getNbErrors())
            er.report("Cardinality of WS:Addressing Header elements", "No errors");

        List<OMElement> hdrs = new ArrayList<> ();
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

        //  - add Appendix V specific requirements
        //		if (action.size() > 0) {
        //			OMElement a = action.getRetrievedDocumentsModel(0);
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

        if (wsaction != null) {
            er.report("WS-Action", wsaction);
            if (!wsaction.equals(wsaction.trim()))
                er.error("", "WS-Action contains whitespace prefix or suffix","", "","");
        }
    }

    void validateHomeCommunityBlock() {
        if (header == null)
            return;

        if (! vc.isXCDR)
            return;

        List<OMElement> homeCommunityBlocks = XmlUtil.childrenWithLocalName(header, "homeCommunityBlock");

        int errCount = er.getNbErrors();
        validateNamespace(homeCommunityBlocks, xdrNamespace);
        if (errCount == er.getNbErrors())
            er.report("Namespace on xdr:homeCommunityBlock", "No errors");

        boolean foundOneHCID = false;
        er.test(homeCommunityBlocks.size() > 0, "", "homeCommunityBlock header cardinality", Integer.toString(homeCommunityBlocks.size()), "0 or 1", "XCDR Supplement: Vol 2, 3.80.1");
        Iterator<OMElement> iterator = homeCommunityBlocks.iterator();
        while (iterator.hasNext()) {
            OMElement hcBlock  = iterator.next();
            Iterator<OMElement> childIterator = hcBlock.getChildElements();
            while (childIterator.hasNext()) {
                OMElement childElement = childIterator.next();
                if (childElement.getQName().equals(new QName("urn:ihe:iti:xdr:2014", "homeCommunityId"))) {
                    String a = childElement.getText();  // This is just for debugging
                    foundOneHCID = true;
                }
            }
        }
        if (! foundOneHCID) {
            err("Did not find xdr:homeCommunityId element within xdr:homeCommunityBlock for XCDR transaction", "XCDR Supplement: Vol 2, 3.80.1");
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

    void validateHeader() {
        er.challenge("Validate SOAP Header");
        if (header == null) {
            er.error("", "Validate SOAP Header", "Missing", "Present", "ITI TF-2x: V.3.2.2 and SOAP Version 1.2 Section 4");
            return;
        }

        OMNamespace ns = header.getNamespace();
        String uri = (ns == null) ? "" : ns.getNamespaceURI();
        er.test(soapEnvelopeNamespace.equals(uri), "", "SOAP Header Namespace", uri, soapEnvelopeNamespace, "http://www.w3.org/TR/soap12-part1/#soapenvelope");

        validateWSAddressing();
        validateHomeCommunityBlock();

        OMElement metadataLevel = XmlUtil.firstChildWithLocalName(header, "metadata-level");
        if (metadataLevel != null) {
            String metadataLevelTxt = metadataLevel.getText();
            if (metadataLevelTxt == null) metadataLevelTxt = "";
            er.detail("metadata-level Header found");
            er.detail("value is " + metadataLevelTxt);
                er.test("urn:direct:addressing".equals(metadataLevel.getNamespace().getNamespaceURI()), "", "metadata-level Namespace", metadataLevel.getNamespace().getNamespaceURI(), "urn:direct:addressing", "");
            if (metadataLevelTxt.equals("minimal")) {
                vc.isXDRMinimal = true;
                er.success("", "metadata-level value", metadataLevelTxt, "minimal or XDS", "");
            } else if (metadataLevelTxt.equals("XDS")) {
                vc.isXDRMinimal = false;
                er.success("", "metadata-level value", metadataLevelTxt, "minimal or XDS", "");
            } else
                er.error("", "metadata-level value", metadataLevelTxt, "minimal or XDS", "");
        }


    }




    OMElement body() {
        er.test(body != null, "", "Body must be present", ((body == null) ? "Missing" : "Found"), "Found", "ITI TF-2x: V.3.2 and SOAP Version 1.2 Section 4");
        if (body == null) return null;

        OMNamespace ns = body.getNamespace();
        String uri = (ns == null) ? "" : ns.getNamespaceURI();
        er.test(soapEnvelopeNamespace.equals(uri), "", "SOAP Body namespace", uri, soapEnvelopeNamespace, "http://www.w3.org/TR/soap12-part1/#soapenvelope");

        List<String> kids = XmlUtil.childrenLocalNames(body);
        er.test(kids.size() == 1, "", "SOAP Body must have a single child", Integer.toString(kids.size()), "1", "http://www.w3.org/TR/soap12-part1/#soapenvelope");
        if (kids.size() == 0)
            return null;
        return body.getFirstElement();
    }

    void validateEnvelope() {
        OMNamespace ns = envelope.getNamespace();
        String uri = (ns == null) ? "" : ns.getNamespaceURI();
        if (uri == null) uri = "";
        er.test(soapEnvelopeNamespace.equals(uri), "", "SOAP Envelope Namespace", uri, soapEnvelopeNamespace, "ITI TF-2x: V.3.2.1.3 and http://www.w3.org/TR/soap12-part1/#soapenvelope");

        String eleName = envelope.getLocalName();
        er.test(eleName.equals("Envelope"), "", "SOAP Envelope Name", eleName, "Envelope", "http://www.w3.org/TR/soap12-part1/#soapenvelope");
    }

    @Override
    public boolean isSystemValidator() { return true; }


}
