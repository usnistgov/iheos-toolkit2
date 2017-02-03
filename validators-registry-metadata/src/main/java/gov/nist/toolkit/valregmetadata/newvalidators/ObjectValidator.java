package gov.nist.toolkit.valregmetadata.newvalidators;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;

/**
 *
 */
public interface ObjectValidator {
    void validateSlotsLegal(ErrorRecorder er);
    void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc);
    void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc);
}
