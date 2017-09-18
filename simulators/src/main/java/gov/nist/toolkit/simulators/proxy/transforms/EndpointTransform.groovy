package gov.nist.toolkit.simulators.proxy.transforms

import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.simulators.proxy.util.HttpRequestBuilder
import gov.nist.toolkit.simulators.proxy.util.RequestTransform
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import org.apache.http.HttpRequest
import org.apache.http.RequestLine
import org.apache.http.message.BasicRequestLine

/**
 *
 */
class EndpointTransform implements RequestTransform {

    HttpRequest run(SimProxyBase base, HttpRequest request) {
        base.setTargetType(base.clientActorType, base.clientTransactionType)

        String targetEndpointString = base.targetSite.getEndpoint(base.clientTransactionType, base.isSecure(), false)
        SimEndpoint targetEndpoint = new SimEndpoint(targetEndpointString)
        RequestLine requestLine = new BasicRequestLine(request.requestLine.method, targetEndpoint.service, request.requestLine.protocolVersion)
        return HttpRequestBuilder.build(request, requestLine)
    }
}
