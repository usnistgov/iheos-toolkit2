package gov.nist.toolkit.testengine.engine

import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

abstract class AbstractValidater<T> {
    String filterDescription
    StringBuilder log = new StringBuilder()
    boolean errors = false

    abstract ValidaterResult validate(T transaction)

    void reset() {
        log = new StringBuilder()
        errors = false
    }

    void log(String msg) {
        log.append(msg).append('\n')
    }

    void error(String msg) {
        log("Error: ${msg}")
        errors = true
    }

    void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription
    }

    String getLog() {
        log.toString()
    }
}
