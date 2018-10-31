package gov.nist.toolkit.testengine.engine.validations.soap

import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

abstract class AbstractSoapValidater extends AbstractValidater<SoapSimulatorTransaction> {
    SimReference simReference

    abstract ValidaterResult validate(SoapSimulatorTransaction sst)

    void setSimReference(SimReference simReference) {
        this.simReference = simReference
    }

    AbstractSoapValidater() {
    }

}
