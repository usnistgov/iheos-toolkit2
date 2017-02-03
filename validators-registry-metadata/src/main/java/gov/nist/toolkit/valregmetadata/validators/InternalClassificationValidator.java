package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valregmetadata.model.InternalClassification;
import gov.nist.toolkit.valsupport.client.ValidationContext;

/**
 *
 */
public class InternalClassificationValidator {
    InternalClassification mo;

    public InternalClassificationValidator(InternalClassification mo) {
        this.mo = mo;
    }

    public void validateRequiredSlotsPresent(ErrorRecorder er,
                                             ValidationContext vc) {
    }

    public void validateSlotsCodedCorrectly(ErrorRecorder er,
                                            ValidationContext vc) {
    }

    public void validateSlotsLegal(ErrorRecorder er) {
    }

}
