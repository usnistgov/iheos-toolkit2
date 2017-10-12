package gov.nist.toolkit.validatorsSoapMessage.factories;

import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactory2I;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.validatorsSoapMessage.message.HttpMessageValidator;
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageParser;
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator;
import gov.nist.toolkit.valregmsg.validation.factories.CommonMessageValidatorFactory;
import gov.nist.toolkit.valregmsg.validation.factories.RootElementValidatorFactory;
import gov.nist.toolkit.valregmsg.validation.factories.ValUtil;
import gov.nist.toolkit.valregmsg.validation.factories.ValidationContextValidationFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

/**
 *
 */
public class SoapMessageValidatorFactory implements MessageValidatorFactory2I {
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

        return ValidationContextValidationFactory.validateBasedOnValidationContext(erBuilder, xml, mvc, vc, rvi);
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
            ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", ExceptionUtil.exception_details(e));
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
        return RootElementValidatorFactory.validateBasedOnRootElement(erBuilder, xml, mvc, vc, rootElementName, rvi);
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
    * Start a new validation on a pre-parsed XML
    * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
    * @param mvc validation engine to use.  If null then create a new one
    * @param vc description of the validations to be performed
    * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
            * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with
    * at least the first validation step so calling engine.run() will kick start the validation. Note that no
    * ValidationContext is created so the goals of the validation are not yet known.
    */
    static private MessageValidatorEngine getValidatorContext(ErrorRecorderBuilder erBuilder, OMElement xml, MessageValidatorEngine mvc, String title, ValidationContext vc, RegistryValidationInterface rvi) {
        String rootElementName = xml.getLocalName();
        if (vc == null)
            vc = DefaultValidationContextFactory.validationContext();

        if (mvc == null)
            mvc = new MessageValidatorEngine();

        if (title == null)
            title = "";


        if (vc.isValid() && (vc.hasSoap || vc.hasSaml)) {
            ValUtil.reportParseDecision(erBuilder, mvc, "Parse Decision", "Running requested validation - " +
                    ((vc.isMessageTypeKnown()) ? vc.getTransactionName() : "Unknown message type") +
                    ((vc.hasSoap) ? " with SOAP Wrapper" : ""));
            mvc.addMessageValidator("SOAP Message Parser", new SoapMessageParser(vc, xml), erBuilder.buildNewErrorRecorder());
            mvc.addMessageValidator("SOAP Message Validator", new SoapMessageValidator(vc, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;

        } else {
            // Parse based on rootElementName
            return RootElementValidatorFactory.validateBasedOnRootElement(erBuilder, xml, mvc, vc,
                    rootElementName, rvi);
        }
    }

    /**
     * Start a new validation where the input comes from a byte[]
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param input byte[]
     * @param mvc validation engine to use.  If null then create a new one
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with
     * at least the first validation step so calling engine.run() will kick start the validation. Note that no
     * ValidationContext is created so the goals of the validation are not yet known.
     */
    static public MessageValidatorEngine getValidatorContext(ErrorRecorderBuilder erBuilder, byte[] input, MessageValidatorEngine mvc, String title, ValidationContext vc, RegistryValidationInterface rvi) {
        OMElement xml = null;
        try {
            // for now all the message inputs are XML - later some will be HTTP wrapped around XML
            String inputString = new String(input);
            xml = Util.parse_xml(inputString);
            xml.build();
        } catch (Exception e) {
            if (mvc == null)
                mvc = new MessageValidatorEngine();
            ErrorRecorder er = ValUtil.reportError(erBuilder, mvc, "XML Parser", ExceptionUtil.exception_details(e));
            if (input == null)
                er.detail("Input was null");
            else {
                String in = new String(input);
                er.detail("Input looks like: " + in.substring(0, ValUtil.min(50, in.length())));
            }
            return mvc;
        }
        return getValidatorContext(erBuilder, xml, mvc, title, vc, rvi);
    }

    /**
     * Start a new validation where input is known to be an HTTP message
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param httpInput HTTP input string
     * @param mvc validation engine to use.  If null then create a new one
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with
     * at least the first validation step so calling engine.run() will kick start the validation. Note that no
     * ValidationContext is created so the goals of the validation are not yet known.
     */
    static public MessageValidatorEngine getValidatorForHttp(ErrorRecorderBuilder erBuilder, String httpInput, MessageValidatorEngine mvc, ValidationContext vc, RegistryValidationInterface rvi) {
        try {
            HttpParserBa hparser = new HttpParserBa(httpInput.getBytes());
            mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
            mvc.addMessageValidator("HTTP Validator", new HttpMessageValidator(vc, hparser, erBuilder, mvc, rvi), erBuilder.buildNewErrorRecorder());
            return mvc;
        } catch (HttpParseException e) {
            mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
            String msg = "Input does not parse as an HTTP stream: " + ExceptionUtil.exception_details(e);
            ValUtil.reportError(erBuilder, mvc, "HTTP Parser", msg + ExceptionUtil.exception_details(e));
            return mvc;
        } catch (ParseException e) {
            mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
            String msg = "Input does not parse as an HTTP stream: " + ExceptionUtil.exception_details(e);
            ValUtil.reportError(erBuilder, mvc, "HTTP Parser", msg + ExceptionUtil.exception_details(e));
            return mvc;
        }
    }

    /**
     * Start a new validation where the input comes from a string.
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param input input string
     * @param vc description of the validations to be performed
     * @param rvi interface for performing local inquires about metadata. Example: does this UUID represent a folder?
     * @return new MessageValidatorEngine which will manage the individual validation steps. It is preloaded with
     * at least the first validation step so calling engine.run() will kick start the validation. Note that no
     * ValidationContext is created so the goals of the validation are not yet known.
     */
    public MessageValidatorEngine getValidator(ErrorRecorderBuilder erBuilder, byte[] input, byte[] directCertInput, ValidationContext vc, RegistryValidationInterface rvi) {

        MessageValidatorEngine mvc = new MessageValidatorEngine();
        if (erBuilder != null) {
            ErrorRecorder er = ValUtil.report(erBuilder, mvc, "Requested Validation Context");
            er.detail(vc.toString());
        }

        logger.debug("ValidationContext is " + vc.toString());

        String inputString = new String(input).trim();

        if (vc.hasHttp) {
            return getValidatorForHttp(erBuilder, inputString, mvc, vc, rvi);
        }

        return new CommonMessageValidatorFactory().getValidator(erBuilder, input, directCertInput, vc, rvi);
    }


}
