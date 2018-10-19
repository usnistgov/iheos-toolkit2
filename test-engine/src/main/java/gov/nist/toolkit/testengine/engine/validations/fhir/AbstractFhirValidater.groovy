package gov.nist.toolkit.testengine.engine.validations.fhir

import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

abstract class AbstractFhirValidater extends AbstractValidater<FhirSimulatorTransaction> {
    SimReference simReference

    abstract ValidaterResult validate(FhirSimulatorTransaction transactionInstance)

    void setSimReference(SimReference simReference) {
        this.simReference = simReference
    }

    AbstractFhirValidater() {
    }

}
