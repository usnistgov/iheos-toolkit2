package gov.nist.toolkit.testengine.engine.validations.xds

import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction

abstract class AbstractSoapValidater extends AbstractValidater<SoapSimulatorTransaction> {
    SimReference simReference


    void setSimReference(SimReference simReference) {
        this.simReference = simReference
    }

    AbstractSoapValidater() {
    }

}
