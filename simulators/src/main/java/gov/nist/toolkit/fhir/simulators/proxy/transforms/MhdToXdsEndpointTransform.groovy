package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.actortransaction.server.EndpointParser
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.simulators.proxy.util.HttpRequestBuilder
import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase
import gov.nist.toolkit.simcoresupport.proxy.util.SimpleRequestTransform
import org.apache.http.HttpRequest
import org.apache.http.RequestLine
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.message.BasicHttpRequest
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
        if (theRequest instanceof BasicHttpEntityEnclosingRequest) {
            BasicHttpEntityEnclosingRequest request = theRequest
            if (request.requestLine.method.equalsIgnoreCase('post')) {
                // this allows endpoint to be chosen
                base.setTargetType(ActorType.REPOSITORY, TransactionType.PROVIDE_AND_REGISTER)

                EndpointParser targetEndpoint = new EndpointParser(base.getTargetEndpoint())
                RequestLine requestLine = new BasicRequestLine(request.requestLine.method, targetEndpoint.service, request.requestLine.protocolVersion)
                return HttpRequestBuilder.build(request, requestLine)
            }
        }
        RequestLine requestLine
        if (theRequest instanceof BasicHttpRequest) {
            BasicHttpRequest request = theRequest
            if (base.endpoint.transactionType == TransactionType.READ_BINARY) {
                base.setTargetType(ActorType.REPOSITORY, TransactionType.RETRIEVE)
                EndpointParser targetEndpoint = new EndpointParser(base.getTargetEndpoint())
                requestLine = new BasicRequestLine('POST', targetEndpoint.service, request.requestLine.protocolVersion)
            } else {
                base.setTargetType(ActorType.REGISTRY, TransactionType.STORED_QUERY)
                EndpointParser targetEndpoint = new EndpointParser(base.getTargetEndpoint())
                requestLine = new BasicRequestLine('POST', targetEndpoint.service, request.requestLine.protocolVersion)
            }
            return HttpRequestBuilder.build(request, requestLine)
        }
        return theRequest
    }
}
