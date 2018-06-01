package gov.nist.toolkit.testengine.engine

abstract class AbstractValidater {
    String filterDescription
    StringBuilder log = new StringBuilder()
    boolean errors = false

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
}
