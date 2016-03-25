package gov.nist.toolkit.valregmsg.validation.factories;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.ServiceRequestContainer;

/**
 *
 */
class ValUtil {
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
        er.err(XdsErrorCode.Code.XDSRegistryError, error, "CommonMessageValidatorFactory", "");
        mvc.addMessageValidator(title, new ServiceRequestContainer(DefaultValidationContextFactory.validationContext()), er);
        return er;
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
        mvc.addMessageValidator(title, new ServiceRequestContainer(DefaultValidationContextFactory.validationContext()), er);
        return er;
    }

    /**
     * Create new ErrorRecorder and use it to report a significant parse decision
     * @param erBuilder ErrorRecorder factory. A new ErrorRecorder is allocated and used for each validation step.
     * @param mvc validation engine to use
     * @param title name of decision
     * @param text text of decision
     */
    static void reportParseDecision(ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, String title, String text) {
        ErrorRecorder er = erBuilder.buildNewErrorRecorder();
        //		er.info1(text);
        mvc.addMessageValidator(title + " - " + text, new ServiceRequestContainer(DefaultValidationContextFactory.validationContext()), er);
    }

    static int min(int a, int b) {
        if (a < b) return a;
        return b;
    }

}
