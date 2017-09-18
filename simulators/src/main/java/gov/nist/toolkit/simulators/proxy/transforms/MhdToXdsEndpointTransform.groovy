package gov.nist.toolkit.simulators.proxy.transforms

import gov.nist.toolkit.simulators.proxy.util.RequestTransform
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import org.apache.http.HttpRequest

/**
 *
 */
class MhdToXdsEndpointTransform implements RequestTransform {
    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) {
        return null
    }
}
