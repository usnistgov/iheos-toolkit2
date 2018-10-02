package gov.nist.toolkit.valregmetadata.top;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

public class NullCustomMetadataValidator extends AbstractCustomMetadataValidator {
    public NullCustomMetadataValidator(Metadata m, ValidationContext vc, RegistryValidationInterface rvi) {
        super(m, vc, rvi);
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        System.out.println("run");
    }
}
