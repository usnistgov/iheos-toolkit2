package gov.nist.toolkit.testengine.engine

import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

abstract class AbstactClientValidater<T> extends AbstractValidater<T> {
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
        log("Error: ${type}:  ${msg}")
        errors = true
    }

    String getLog() {
        log.toString()
    }

}
