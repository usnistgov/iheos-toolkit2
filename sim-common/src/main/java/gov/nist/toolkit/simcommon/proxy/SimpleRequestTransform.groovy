package gov.nist.toolkit.simcommon.proxy

import gov.nist.toolkit.simcommon.proxy.SimProxyBase
import gov.nist.toolkit.simcommon.proxy.ReturnableErrorException
import org.apache.http.HttpRequest

/**
 *
 */
interface SimpleRequestTransform {
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException
}
