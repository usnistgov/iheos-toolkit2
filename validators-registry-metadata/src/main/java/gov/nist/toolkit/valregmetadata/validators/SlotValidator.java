package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.datatype.FormatValidator;
import gov.nist.toolkit.valregmetadata.datatype.FormatValidatorCalledIncorrectlyException;
import gov.nist.toolkit.valregmetadata.model.Slot;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

/**
 *
 */
public class SlotValidator {
    Slot mo;

    public SlotValidator(Slot mo) {
        this.mo = mo;
    }

    public void validate(ErrorRecorder er, boolean multivalue, FormatValidator validator, String resource) {
        if (!multivalue && mo.getValues().size() > 1)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.getOwnerType() + "(" + mo.getOwnerId() + ") has Slot " + mo.getName() + " which is required to have a single value, " + mo.getValues().size() + "  values found", this, resource);
        try {
            for (String value : mo.getValues()) {
                validator.validate(value);
            }
        } catch (FormatValidatorCalledIncorrectlyException e) {
            // oops - can't call with individual slot values, needs Slot
            try {
                validator.validate(mo.getMyElement());
            } catch (FormatValidatorCalledIncorrectlyException e1) {
                // hmmm - I guess we give up here
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, new XdsInternalException("Slot#validate: the validator " + validator.getClass().getName() + " implements no validate methods"));
            }
        }
    }

}
