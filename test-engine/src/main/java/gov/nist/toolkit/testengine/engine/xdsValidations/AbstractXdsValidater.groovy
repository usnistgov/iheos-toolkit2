package gov.nist.toolkit.testengine.engine.xdsValidations

import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.SimulatorTransaction
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.fhirValidations.ValidaterResult

abstract class AbstractXdsValidater extends AbstractValidater {
    SimReference simReference

    abstract ValidaterResult validate(SimulatorTransaction transaction)

    void setSimReference(SimReference simReference) {
        this.simReference = simReference
    }

    AbstractXdsValidater() {
    }

}
