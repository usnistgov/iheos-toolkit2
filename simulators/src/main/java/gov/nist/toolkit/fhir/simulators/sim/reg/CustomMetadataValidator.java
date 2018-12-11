package gov.nist.toolkit.fhir.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.testengine.engine.validations.registry.AbstractServerValidater;
import gov.nist.toolkit.testengine.engine.validations.registry.ValidatorLookup;
import gov.nist.toolkit.valregmetadata.top.AbstractCustomMetadataValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

public class CustomMetadataValidator extends AbstractCustomMetadataValidator {
    String validatorClassName;
    String environment;
    TestSession testSession;

    public CustomMetadataValidator(String validatorClassName, Metadata m, ValidationContext vc, RegistryValidationInterface rvi, String environment, TestSession testSession) {
        super(m, vc, rvi);
        this.validatorClassName = validatorClassName;
        this.environment = environment;
        this.testSession = testSession;
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        AbstractServerValidater val = ValidatorLookup.find(validatorClassName, environment, testSession);

        Metadata m = getM();
        val.doRun(m, er);
    }
}
