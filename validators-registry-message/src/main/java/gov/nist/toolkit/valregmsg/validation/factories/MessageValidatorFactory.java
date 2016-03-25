package gov.nist.toolkit.valregmsg.validation.factories;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valregmsg.message.*;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageBody;
import gov.nist.toolkit.valsupport.message.MessageBodyContainer;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

/**
 * This does not handle SOAP wrapper.  For that use SoapMessageValidatorFactory.
 */
public class MessageValidatorFactory {
    static Logger logger = Logger.getLogger(MessageValidatorFactory.class);

    /**
     * Start a new validation on a string input and run the validation inferred from the name of the XML root element
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param body input string form of the XML
     * @param mvc validation engine to use.  If null then create a new one
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return the MessageValidatorEngine after it has run to completion
     */
    public static MessageValidatorEngine validateBasedOnRootElement(
            ErrorRecorderBuilder erBuilder, String body,
            MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
        OMElement xml = null;
        try {
            xml = Util.parse_xml(body);
            xml.build();
        } catch (Exception e) {
            ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", e.getMessage());
            if (body == null)
                er.detail("Input was null");
            else
                er.detail("Input looks like: " + body.substring(0, ValUtil.min(100, body.length())));
            return mvc;
        }
        String rootElementName = xml.getLocalName();
        return validateBasedOnRootElement(erBuilder, xml, mvc, vc, rootElementName, rvi);

    }

    /**
     * Start a new validation on an XML input and run the validation inferred from the name of the XML root element
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param xml input XML
     * @param mvc validation engine to use.  If null then create a new one
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return the MessageValidatorEngine after it has run to completion
     */
    public static MessageValidatorEngine validateBasedOnRootElement(
            ErrorRecorderBuilder erBuilder, OMElement xml,
            MessageValidatorEngine mvc, ValidationContext vc,
            String rootElementName, RegistryValidationInterface rvi) {
        if (rootElementName.equals("ProvideAndRegisterDocumentSetRequest")) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Provide and Register request");
            vc.isPnR = true;
            vc.isRequest = true;
            mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("DocumentContentsExtraction", new DocumentAttachmentMapper(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("SubmitObjectsRequest")) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Register request");
            // TODO: In this case, it could be a Register On-Demand Document Entry (RODDE) type so we need to dig a little deeper.
            // Will this work?
            // new AXIOMXPath("/SubmitObjectsRequest/LeafRegistryObjectList[1]/ExtrinsicObject[1][@objectType='urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248']")
            vc.isR = true;
            vc.isRequest = true;
            mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("RegistryResponse")) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a RegistryResponse");
            vc.isR = true; // could also be PnR or XDR - doesn't matter
            vc.isResponse = true;
            mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("AdhocQueryRequest")) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Stored Query request");
            vc.isSQ = true;
            vc.isRequest = true;
            mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("QueryRequest", new QueryRequestMessageValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("AdhocQueryResponse")) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Stored Query response");
            vc.isSQ = true;
            vc.isRequest = false;
            vc.isResponse = true;
            mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("AdhocQueryResponse", new QueryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            // need to inspect WSAction to know if it is XC
            mvc.addMessageValidator("Contained Metadata", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("RetrieveDocumentSetRequest")) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Retrieve Document Set request");
            vc.isRet = true;
            vc.isRequest = true;
            mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("RetrieveDocumentSetRequest", new RetrieveRequestValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("RetrieveDocumentSetResponse")) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Input is a Retrieve Document Set response");
            vc.isRet = true;
            vc.isResponse = true;
            mvc.addMessageValidator("DocumentContentsExtraction", new DocumentAttachmentMapper(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("RetrieveDocumentSetResponse", new RetrieveResponseValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("TestResults")) {
            return CommonMessageValidatorFactory.getValidatorContextForTestLog(erBuilder, xml, rvi);
        } else if (rootElementName.equals("Envelope")) {
//            // schema validation of SOAP envelope is useless
//            //			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
//            // don't know what ValidationContext to set - let this validator choose
//            mvc.addMessageValidator("SOAP Message Parser", new SoapMessageParser(vc, xml), erBuilder.buildNewErrorRecorder());
//            mvc.addMessageValidator("SOAP Message Validator", new SoapMessageValidator(vc, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            ValUtil.reportError(erBuilder, mvc, rootElementName, "Wrong validator called - use SoapMessageValidatorFactory for SOAP messages.");
            return mvc;
        } else if (rootElementName.equals("Assertion")) {
            // schema validation of SOAP envelope is useless
            //			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            // don't know what ValidationContext to set - let this validator choose
            mvc.addMessageValidator("SAML Wrapper", new SAMLMessageValidator(vc, xml, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;

        } else if (rootElementName.equals("PRPA_IN201305UV02")) {
            mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("PRPA_IN201306UV02")) {
            mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (rootElementName.equals("ClinicalDocument")) {
            mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else {
            ValUtil.reportError(erBuilder, mvc, rootElementName, "Don't know how to parse this.");
            return mvc;
        }
    }

    /**
     * Start a new validation on an XML input and run the validation based on the rules coded in the ValidationContext
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param xml input XML
     * @param mvc validation engine to use.  If null then create a new one
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return the MessageValidatorEngine after it has run to completion
     */
    public static MessageValidatorEngine validateBasedOnValidationContext(
            ErrorRecorderBuilder erBuilder, OMElement xml,
            MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
        logger.debug("messageValidatorEngine#validateBasedOnValidationContext");
        logger.debug(" VC: " + vc.toString());

        String rootElementName = null;

        if (xml != null)
            rootElementName = xml.getLocalName();

        //XcpdInit, XcpdResp and C32 won't use these schema validators

        if (!vc.isXcpd && !vc.isC32 && !vc.isNcpdp ) {

            if (vc.hasMetadata()) {
                mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("Parse Metadata Wrappers", new WrapperValidator(vc), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("Validate Metadata Element Ordering", new MetadataOrderValidator(vc), erBuilder.buildNewErrorRecorder());
            }

            if (vc.containsDocuments()) {
                mvc.addMessageValidator(DocumentAttachmentMapper.class.getSimpleName(), new DocumentAttachmentMapper(vc, xml), erBuilder.buildNewErrorRecorder());
            }

            mvc.addMessageValidator("Schema Validator", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
        }
//        if( vc.hasSaml )
//            mvc.addMessageValidator("SAML Validator", new SAMLMessageValidator(vc, rootElement, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());

        // parse based on Validation Context
        if (vc.isPnR) {
            if (vc.isXDR) {
                if (vc.isRequest) {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "ProvideAndRegisterDocumentSetRequest", rootElementName);
                    mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                    return mvc;
                } else {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                    mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                    return mvc;
                }
            } else {
                if (vc.isRequest) {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "ProvideAndRegisterDocumentSetRequest", rootElementName);
                    mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                    if (vc.hasHttp)
                        mvc.addMessageValidator("DocumentElementValidator", new DocumentElementValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                    return mvc;
                } else {
                    CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                    mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                    return mvc;
                }
            }
        } else if (vc.isR || vc.isRODDE) {
            if (vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
                mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            }
        } else if (vc.isMU) {
            if (vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
                mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RegistryResponse", rootElementName);
                mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            }
        } else if (vc.isRet) {
            if (vc.isRequest) {
                mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetRequest", rootElementName);
                mvc.addMessageValidator("RetrieveDocumentSetRequest", new RetrieveRequestValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetResponse", rootElementName);
                mvc.addMessageValidator("RetrieveDocumentSetResponse", new RetrieveResponseValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            }

        } else if (vc.isRad69) {
            if (vc.isRequest) {
                mvc.addMessageValidator("Message Body Container", new MessageBodyContainer(vc, xml), erBuilder.buildNewErrorRecorder());
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveImagingDocumentSetRequest", rootElementName);
                mvc.addMessageValidator("RetrieveImagingDocumentSetRequest", new RetrieveImagingDocumentSetRequestValidator(vc, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "RetrieveDocumentSetResponse", rootElementName);
                mvc.addMessageValidator("RetrieveDocumentSetResponse", new RetrieveResponseValidator(vc, xml, erBuilder, mvc), erBuilder.buildNewErrorRecorder());
                return mvc;
            }



        } else if (vc.isSQ) {
            if (vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "AdhocQueryRequest", rootElementName);
                mvc.addMessageValidator("QueryRequest", new QueryRequestMessageValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "AdhocQueryResponse", rootElementName);
                mvc.addMessageValidator("AdhocQueryResponse", new QueryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                mvc.addMessageValidator("Contained Metadata", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
                return mvc;
            }
        } else if (vc.isXDM) {
            CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "SubmitObjectsRequest", rootElementName);
            mvc.addMessageValidator("SubmitObjectsRequest", new MetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else if (vc.isXcpd || vc.isNwHINxcpd) {
            if (vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "PRPA_IN201305UV02", rootElementName);
                mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "PRPA_IN201306UV02", rootElementName);
                mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            }
        } else if (vc.isNcpdp) {
            if(vc.isRequest) {
                CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "Message", rootElementName);
                mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
                return mvc;
            } else{
                return mvc;
            }
        } else if (vc.isC32) {
            CommonMessageValidatorFactory.validateToplevelElement(erBuilder, mvc, "ClinicalDocument", rootElementName);
            mvc.addMessageValidator("Schematron Validator", new SchematronValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            return mvc;
        } else {
            ValUtil.reportError(erBuilder, mvc, "ValidationContext", "Don't know how to parse this: " + vc.toString());
            return mvc;
        }
    }

    /**
     * Start a new validation on a String input and run the validation based on the rules coded in the ValidationContext
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param body input XML string
     * @param mvc validation engine to use.  If null then create a new one
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return the MessageValidatorEngine after it has run to completion
     */
    public static MessageValidatorEngine validateBasedOnValidationContext(
            ErrorRecorderBuilder erBuilder, String body,
            MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
        OMElement xml = null;
        try {
            // for now all the message inputs are XML - later some will be HTTP wrapped around XML
            xml = Util.parse_xml(body);
            xml.build();
        } catch (Exception e) {
            ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", e.getMessage());
            if (body == null)
                er.detail("Input was null");
            else
                er.detail("Input looks like: " + body.substring(0, ValUtil.min(100, body.length())));
            return mvc;
        }
        return validateBasedOnValidationContext(erBuilder, xml, mvc, vc, rvi);
    }


}
