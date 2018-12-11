package gov.nist.toolkit.testengine.engine.validations

import groovy.transform.ToString

@ToString
class ValidaterResult {
    /**
     * FhirSimulatorTransaction or (Xds)SimulatorTransaction
     */
    def transaction
    /**
     * AbstractFhirValidater  or AbstractXdsValidator
     */
    def filter
    boolean match
    private StringBuilder log = new StringBuilder()

    ValidaterResult(def transaction, def filter, boolean match) {
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
