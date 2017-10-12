package gov.nist.toolkit.simulators.proxy.util


import org.apache.http.HttpRequest
import org.apache.http.RequestLine
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.message.BasicHttpRequest

/**
 *
 */
class HttpRequestBuilder {

    static HttpRequest build(HttpRequest request, RequestLine requestLine) {
        if (request instanceof BasicHttpEntityEnclosingRequest) {
            BasicHttpEntityEnclosingRequest newrequest = new BasicHttpEntityEnclosingRequest(requestLine)
            newrequest.setHeaders(request.allHeaders)
            newrequest.setEntity(((BasicHttpEntityEnclosingRequest) request).getEntity())
            return newrequest
        } else if (request instanceof BasicHttpRequest) {
            BasicHttpRequest newRequest = new BasicHttpRequest(requestLine)
            newRequest.setHeaders(request.allHeaders)
            return newRequest
        }
        else
            assert true, "Cannot clone ${request.getClass().getName()}"
    }
}
