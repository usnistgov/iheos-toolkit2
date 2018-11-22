package gov.nist.toolkit.valsupport.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

public class ForcedErrorMessageValidator extends AbstractMessageValidator {
    private XdsErrorCode.Code errorCode;


    public ForcedErrorMessageValidator(ValidationContext vc, XdsErrorCode.Code errorCode) {
        super(vc);
        this.errorCode = errorCode;
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        if (errorCode != null)
            er.err(errorCode, "Error was forced by configuration", null, null);
    }
}
