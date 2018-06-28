package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import groovy.transform.ToString

@ToString
class ValidaterResult {
    FhirSimulatorTransaction transaction
    AbstractFhirValidater filter
    boolean match
    private StringBuilder log = new StringBuilder()

    ValidaterResult(FhirSimulatorTransaction transaction, AbstractFhirValidater filter, boolean match) {
        this.transaction = transaction
        this.filter = filter
        this.match = match
    }

    def log(String msg) {
        log.append(msg).append('\n')
    }

    String getLog() {
        log.toString()
    }

}
