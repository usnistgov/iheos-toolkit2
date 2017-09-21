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
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

/**
 * Run validation based on examining the message and deciding what validators
 * are needed.
 */
public class NewRootElementValidatorFactory {
    static Logger logger = Logger.getLogger(NewRootElementValidatorFactory.class);

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
            ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", ExceptionUtil.exception_details(e));
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
            mvc.addMessageValidator("ProvideAndRegisterDocumentSetRequest", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
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
            mvc.addMessageValidator("SubmitObjectsRequest", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
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
            mvc.addMessageValidator("Contained Metadata", new NewMetadataMessageValidator(vc, new MessageBody(xml), erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
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
        }
        else {
            ValUtil.reportError(erBuilder, mvc, rootElementName, "Don't know how to parse this.");
            return mvc;
        }
    }

    //    /**
//     * Start a new validation on a String input and run the validation based on the rules coded in the ValidationContext
//     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
//     * @param body input XML string
//     * @param mvc validation engine to use.  If null then create a new one
//     * @param vc description of the validations to be performed
//     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
//     * @return the MessageValidatorEngine after it has run to completion
//     */
//    public static MessageValidatorEngine validateBasedOnValidationContext(
//            ErrorRecorderBuilder erBuilder, String body,
//            MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
//        OMElement xml = null;
//        try {
//            // for now all the message inputs are XML - later some will be HTTP wrapped around XML
//            xml = Util.parse_xml(body);
//            xml.build();
//        } catch (Exception e) {
//            ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", e.getMessage());
//            if (body == null)
//                er.detail("Input was null");
//            else
//                er.detail("Input looks like: " + body.substring(0, ValUtil.min(100, body.length())));
//            return mvc;
//        }
//        return validateBasedOnValidationContext(erBuilder, xml, mvc, vc, rvi);
//    }


}
