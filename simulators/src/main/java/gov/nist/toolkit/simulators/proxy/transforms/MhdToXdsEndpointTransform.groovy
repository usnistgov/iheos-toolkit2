package gov.nist.toolkit.simulators.proxy.transforms

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.server.EndpointParser
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simulators.proxy.util.HttpRequestBuilder
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.simulators.proxy.util.SimpleRequestTransform
import org.apache.http.HttpRequest
import org.apache.http.RequestLine
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.message.BasicRequestLine
import org.apache.log4j.Logger
/**
 *
 */
class MhdToXdsEndpointTransform implements SimpleRequestTransform {
    static private final Logger logger = Logger.getLogger(MhdToXdsEndpointTransform.class);

    @Override
    HttpRequest run(SimProxyBase base, HttpRequest theRequest) {
        logger.info('Running MhdToXdsEndpointTransform')
        assert theRequest instanceof BasicHttpEntityEnclosingRequest
        BasicHttpEntityEnclosingRequest request = theRequest
        if (request.requestLine.method.equalsIgnoreCase('post')) {
            // this allows endpoint to be chosen
            base.setTargetType(ActorType.REPOSITORY, TransactionType.PROVIDE_AND_REGISTER)

            EndpointParser targetEndpoint = new EndpointParser(base.getTargetEndpoint())
            RequestLine requestLine = new BasicRequestLine(request.requestLine.method, targetEndpoint.service, request.requestLine.protocolVersion)
            return HttpRequestBuilder.build(request, requestLine)
        }
        return request
    }
}
