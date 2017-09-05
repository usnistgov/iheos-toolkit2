package gov.nist.toolkit.fhir.mhd.errors

import gov.nist.toolkit.fhir.mhd.ErrorLogger
import gov.nist.toolkit.xdsexception.ExceptionUtil

/**
 *
 */
abstract class AbstractError {
    String reference
    String stackTrace

    AbstractError(ErrorLogger errorLogger) {
        errorLogger.add(this)
        stackTrace = ExceptionUtil.here('Error detected here')
    }

    String toString() {
        getClass().simpleName + ': ' + reference + '\n' + stackTrace
    }

}
