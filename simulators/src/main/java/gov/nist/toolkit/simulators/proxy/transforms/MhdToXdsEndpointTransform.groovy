package gov.nist.toolkit.simulators.proxy.transforms

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.simulators.proxy.util.HttpRequestBuilder
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.simulators.proxy.util.SimpleRequestTransform
import org.apache.http.HttpRequest
import org.apache.http.RequestLine
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.message.BasicRequestLine
/**
 *
 */
class MhdToXdsEndpointTransform implements SimpleRequestTransform {
    @Override
    HttpRequest run(SimProxyBase base, HttpRequest theRequest) {
        assert theRequest instanceof BasicHttpEntityEnclosingRequest
        BasicHttpEntityEnclosingRequest request = theRequest
        if (request.requestLine.method.equalsIgnoreCase('post')) {
            base.setTargetType(ActorType.REPOSITORY, TransactionType.PROVIDE_AND_REGISTER)
            SimEndpoint targetEndpoint = base.getEndpoint()
            RequestLine requestLine = new BasicRequestLine(request.requestLine.method, targetEndpoint.service, request.requestLine.protocolVersion)
            return HttpRequestBuilder.build(request, requestLine)
        }
        return request
    }
}
