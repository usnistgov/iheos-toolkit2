package gov.nist.toolkit.simulators.proxy.transforms

import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.simulators.proxy.util.HttpRequestBuilder
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.simulators.proxy.util.SimpleRequestTransform
import org.apache.http.HttpRequest
import org.apache.http.RequestLine
import org.apache.http.message.BasicRequestLine
import org.apache.log4j.Logger

/**
 * basic pass through - update endpoint to match target system configuration
 */
class NullEndpointTransform implements SimpleRequestTransform {
    static private final Logger logger = Logger.getLogger(NullEndpointTransform.class);

    HttpRequest run(SimProxyBase base, HttpRequest request) {
        logger.info('Running NullEndpointTransform')
        base.setTargetType(base.clientActorType, base.clientTransactionType)

        String targetEndpointString = base.getEndpoint()
        SimEndpoint targetEndpoint = new SimEndpoint(targetEndpointString)
        RequestLine requestLine = new BasicRequestLine(request.requestLine.method, targetEndpoint.service, request.requestLine.protocolVersion)
        return HttpRequestBuilder.build(request, requestLine)
    }
}
