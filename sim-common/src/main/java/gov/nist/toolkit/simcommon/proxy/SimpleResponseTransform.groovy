package gov.nist.toolkit.simcommon.proxy

import gov.nist.toolkit.simcommon.proxy.SimProxyBase
import org.apache.http.HttpResponse

/**
 *
 */
interface SimpleResponseTransform {
    HttpResponse run(SimProxyBase base, HttpResponse response)
}
