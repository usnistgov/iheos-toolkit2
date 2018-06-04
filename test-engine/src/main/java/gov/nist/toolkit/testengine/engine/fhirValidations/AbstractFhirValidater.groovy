package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

abstract class AbstractFhirValidater extends AbstractValidater {
    SimReference simReference

    abstract ValidaterResult validate(FhirSimulatorTransaction transaction)

    void setSimReference(SimReference simReference) {
        this.simReference = simReference
    }

    AbstractFhirValidater() {
    }

}
