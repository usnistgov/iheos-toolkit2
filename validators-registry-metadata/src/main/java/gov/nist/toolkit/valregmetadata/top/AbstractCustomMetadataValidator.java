package gov.nist.toolkit.valregmetadata.top;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

public abstract class AbstractCustomMetadataValidator extends AbstractMessageValidator {
    private Metadata m;
    private RegistryValidationInterface rvi;

    public AbstractCustomMetadataValidator(Metadata m, ValidationContext vc, RegistryValidationInterface rvi) {
        super(vc);
        this.m = m;
        this.rvi = rvi;
    }

    public Metadata getM() {
        return m;
    }

    public RegistryValidationInterface getRvi() {
        return rvi;
    }
}
