package gov.nist.toolkit.simcoresupport.mhd

import gov.nist.toolkit.simcoresupport.mhd.errors.AbstractError

/**
 *
 */
class ErrorLogger {
    def log = []

    def add(AbstractError error) {
        log << error
    }

    int size() { log.size() }

    boolean hasErrors() { size() > 0}

    def asString()  {
        StringBuilder buf = new StringBuilder()
//        log.each { buf.append(it.reference).append('\n')}
        log.each { buf.append(it.toString()).append('\n')}
        return buf.toString()
    }

    def getError(int i) {
        if (i < log.size())
            return log[i]
        return null
    }

    List getErrors() {
        return log
    }
}
