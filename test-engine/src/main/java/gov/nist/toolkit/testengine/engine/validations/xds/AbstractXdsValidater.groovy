package gov.nist.toolkit.testengine.engine.validations.xds

import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.SimulatorTransaction
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

abstract class AbstractXdsValidater extends AbstractValidater<SimulatorTransaction> {
    SimReference simReference


    void setSimReference(SimReference simReference) {
        this.simReference = simReference
    }

    AbstractXdsValidater() {
    }

}
