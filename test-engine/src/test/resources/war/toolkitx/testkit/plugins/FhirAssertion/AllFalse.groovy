package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

class AllTrue extends AbstractFhirValidater {
    @Override
    ValidaterResult validate(FhirSimulatorTransaction transactionInstance) {
        return new ValidaterResult(transactionInstance, this, true)
    }

    AllTrue(SimReference theSimReference, String theFilterDescription) {
        super(theSimReference, theFilterDescription)
    }
}
