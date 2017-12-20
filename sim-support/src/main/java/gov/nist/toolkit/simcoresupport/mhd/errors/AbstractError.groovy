package gov.nist.toolkit.simcoresupport.mhd.errors

import gov.nist.toolkit.simcoresupport.mhd.ErrorLogger
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
