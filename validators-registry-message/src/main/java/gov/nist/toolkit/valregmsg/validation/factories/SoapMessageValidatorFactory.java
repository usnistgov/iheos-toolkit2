package gov.nist.toolkit.valregmsg.validation.factories;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valregmsg.message.SoapMessageParser;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

/**
 *
 */
public class SoapMessageValidatorFactory {
    static Logger logger = Logger.getLogger(SoapMessageValidatorFactory.class);

    public static MessageValidatorEngine validateBasedOnValidationContext(
            ErrorRecorderBuilder erBuilder, OMElement xml,
            MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
        logger.debug("messageValidatorEngine#validateBasedOnValidationContext");
        logger.debug(" VC: " + vc.toString());

        String rootElementName = null;

        if (xml != null)
            rootElementName = xml.getLocalName();

        if (vc.hasSoap) {
            mvc.addMessageValidator("SOAP Message Parser", new SoapMessageParser(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("SOAP Message Validator", new SoapMessageValidator(vc, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        }

        return MessageValidatorFactory.validateBasedOnValidationContext(erBuilder, xml, mvc, vc, rvi);
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

        if (rootElementName.equals("Envelope")) {
            // schema validation of SOAP envelope is useless
            //			mvc.addMessageValidator("Schema", new SchemaValidator(vc, xml), erBuilder.buildNewErrorRecorder());
            // don't know what ValidationContext to set - let this validator choose
            mvc.addMessageValidator("SOAP Message Parser", new SoapMessageParser(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("SOAP Message Validator", new SoapMessageValidator(vc, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        }
        return MessageValidatorFactory.validateBasedOnRootElement(erBuilder, xml, mvc, vc, rootElementName, rvi);
    }

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

}
