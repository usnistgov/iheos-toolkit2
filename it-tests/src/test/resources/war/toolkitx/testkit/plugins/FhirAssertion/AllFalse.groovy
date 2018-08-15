package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

class AllFalse extends AbstractFhirValidater {
    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
        return new ValidaterResult(transaction, this, false)
    }

    AllFalse() {
    }
}
