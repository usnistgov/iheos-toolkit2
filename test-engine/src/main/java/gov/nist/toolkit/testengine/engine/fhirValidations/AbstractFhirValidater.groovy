package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

abstract class AbstractFhirValidater extends AbstractValidater {
    SimReference simReference

    abstract ValidaterResult validate(FhirSimulatorTransaction transaction)

    AbstractFhirValidater(SimReference theSimReference, String theFilterDescription) {
        simReference = theSimReference
        filterDescription = theFilterDescription
    }

}
