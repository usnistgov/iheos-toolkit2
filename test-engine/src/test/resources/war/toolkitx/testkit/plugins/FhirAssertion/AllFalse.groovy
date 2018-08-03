package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.fhirValidations.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.fhirValidations.ValidaterResult

class AllTrue extends AbstractFhirValidater {
    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
        return new ValidaterResult(transaction, this, true)
    }

    AllTrue(SimReference theSimReference, String theFilterDescription) {
        super(theSimReference, theFilterDescription)
    }
}
