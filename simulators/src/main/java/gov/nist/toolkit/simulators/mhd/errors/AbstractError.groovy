package gov.nist.toolkit.simulators.mhd.errors

import gov.nist.toolkit.simulators.mhd.ErrorLogger
import gov.nist.toolkit.xdsexception.ExceptionUtil

/**
 *
 */
abstract class AbstractError {
    String stackTrace

    AbstractError(ErrorLogger errorLogger) {
        errorLogger.add(this)
        stackTrace = ExceptionUtil.here('Error detected here')
    }


}
