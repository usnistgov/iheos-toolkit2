package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.fhirValidations.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.fhirValidations.SimReference
import gov.nist.toolkit.testengine.engine.fhirValidations.ValidaterResult

class RequiresHttps extends AbstractFhirValidater {
    String mustBeHttps

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
        return null
    }

    RequiresHttps(SimReference theSimReference, String theFilterDescription, String mustBeHttps) {
        super(theSimReference, theFilterDescription)
        this.mustBeHttps = mustBeHttps
    }
}
