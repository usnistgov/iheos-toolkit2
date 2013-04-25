package gov.nist.toolkit.valdirfactory;


import gov.nist.registry.common2.direct.DirectDecoder;
import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactory2I;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.NullMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

public class DirectMessageValidatorFactory implements MessageValidatorFactory2I {
	
	/**
	 * Start a new validation where input is known to be a DIRECT message
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param input DIRECT input string
	 * @param mvc validation engine to use.  If null then create a new one
	 * @param vc description of the validations to be performed
	 * @return old (or new) MessageValidatorEngine which will manage the individual validation steps. It is preloaded with 
	 * at least the first validation step so calling engine.run() will kick start the validation. Note that no
	 * ValidationContext is created so the goals of the validation are not yet known.
	 */
	static public MessageValidatorEngine getValidatorForDirect(ErrorRecorderBuilder erBuilder, byte[] input, byte[] certificate, MessageValidatorEngine mvc, ValidationContext vc) {
		mvc = (mvc == null) ? new MessageValidatorEngine() : mvc;
		if (certificate == null) {
			ErrorRecorder er = reportError(erBuilder, mvc, "Direct Validator", "No Certificate");
			return mvc;
		}
		mvc.addMessageValidator("Message Validator", new DirectDecoder(vc, erBuilder, Io.bytesToInputStream(input), Io.bytesToInputStream(certificate)), erBuilder.buildNewErrorRecorder());
		return mvc;
	}

	@Override
	public MessageValidatorEngine getValidator(ErrorRecorderBuilder erBuilder, byte[] input, byte[] directCertInput, ValidationContext vc, RegistryValidationInterface rvi) {
		MessageValidatorEngine mvc = new MessageValidatorEngine();
		if (erBuilder != null) {
			ErrorRecorder er = report(erBuilder, mvc, "Requested Validation Context");
			er.detail(vc.toString());
			er.detail("An issue with the validation display occurred.");
		}

		String inputString = new String(input).trim();

		if (!vc.isMessageTypeKnown() && vc.updateable) {
			if (inputString.startsWith("POST"))
				vc.hasHttp = true;
		}

		if (vc.isDIRECT) {
			mvc = getValidatorForDirect(erBuilder, input, directCertInput, mvc, vc);
		} else {
			MessageValidatorFactory factory = new MessageValidatorFactory();
			mvc = factory.getValidator(erBuilder, input, directCertInput, vc, rvi);
		}


		return mvc;
	}
	
	/**
	 * Create a a newly constructed ErrorRecorder and assign it a title
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param mvc validation engine to use
	 * @param title title of new ErrorRecorder section to be built
	 * @return new ErrorRecorder
	 */
	static ErrorRecorder report(ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, String title) {
		ErrorRecorder er = erBuilder.buildNewErrorRecorder();
		mvc.addMessageValidator(title, new NullMessageValidator(new ValidationContext()), er);
		return er;
	}
	
	/**
	 * Report an error in a newly constructed ErrorRecorder
	 * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
	 * @param mvc validation engine to use
	 * @param title title of new ErrorRecorder section to be built
	 * @param error text of error
	 * @return new ErrorRecorder
	 */
	static ErrorRecorder reportError(ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, String title, String error) {
		ErrorRecorder er = erBuilder.buildNewErrorRecorder();
		er.err(XdsErrorCode.Code.XDSRegistryError, error, "MessageValidatorFactory", "");
		mvc.addMessageValidator(title, new NullMessageValidator(new ValidationContext()), er);
		return er;
	}
	
}
