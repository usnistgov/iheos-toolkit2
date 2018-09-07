package gov.nist.toolkit.testengine.engine

import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.transactions.BasicTransaction

abstract class AbstractValidater<T> {
    String filterDescription
    StringBuilder log = new StringBuilder()
    boolean errors = false

    abstract ValidaterResult validate(T transactionInstance)

    AbstractValidater<T> reset() {
        log = new StringBuilder()
        errors = false
        this
    }

    void log(String msg) {
        log.append(msg).append('\n')
    }

    void error(String msg) {
        log("Error: ${msg}")
        errors = true
    }

    void error(String type, String msg) {
        error("${type} Error: ${msg}")
    }

    void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription
    }

    String getLog() {
        log.toString()
    }
}
