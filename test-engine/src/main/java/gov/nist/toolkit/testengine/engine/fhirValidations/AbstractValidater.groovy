package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

abstract class AbstractValidater {
    SimReference simReference
    String filterDescription
    StringBuilder log = new StringBuilder()
    boolean errors = false

    abstract ValidaterResult validate(FhirSimulatorTransaction transaction)

    AbstractValidater(SimReference theSimReference, String theFilterDescription) {
        simReference = theSimReference
        filterDescription = theFilterDescription
    }

    def log(String msg) {
        log.append(msg).append('\n')
    }

    def error(String msg) {
        log("Error: ${msg}")
        errors = true
    }

    def reset() {
        log = new StringBuilder()
        errors = false
    }
}
