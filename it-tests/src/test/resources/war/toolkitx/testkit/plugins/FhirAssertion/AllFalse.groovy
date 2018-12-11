package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.transactions.BasicTransaction

class AllFalse extends AbstractFhirValidater {
    @Override
    ValidaterResult validate(FhirSimulatorTransaction transactionInstance) {
        return new ValidaterResult(transactionInstance, this, false)
    }

    AllFalse() {
    }
}
