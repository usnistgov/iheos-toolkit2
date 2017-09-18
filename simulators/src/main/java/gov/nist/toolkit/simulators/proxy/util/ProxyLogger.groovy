package gov.nist.toolkit.simulators.proxy.util

import gov.nist.toolkit.simcommon.server.SimDb
import org.apache.http.Header
import org.apache.http.HeaderElement
import org.apache.http.HttpEntity
import org.apache.http.HttpMessage
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair

/**
 *
 */
class ProxyLogger {
    SimDb simDb

    ProxyLogger(SimDb simDb) {
        assert simDb
        this.simDb = simDb
    }

    def logRequest(HttpRequest request) {
        simDb.putRequestHeaderFile(asString(request).bytes)
    }

    def logRequestEntity(byte[] content) {
        simDb.putRequestBodyFile(content);
    }

    def logResponse( HttpResponse response) {
        simDb.putResponseHeaderFile(asString(response).bytes)
    }

    def logResponseEntity(byte[] entity) {
        simDb.putResponseBody(entity)
    }

     String asString(HttpResponse response) {
        StringBuilder buf = new StringBuilder()

        buf.append(response.statusLine.toString())
        asString((HttpMessage) response, buf)

        return buf.toString()
    }

      String asString(HttpRequest request) {
        StringBuilder buf = new StringBuilder()

        buf.append(request.requestLine.toString()).append('\n')
        asString((HttpMessage) request, buf)

        return buf.toString()
    }

    def asString(HttpMessage msg, StringBuilder buf) {

        msg.allHeaders.each { Header header ->
            buf.append(header.name).append(': ').append(header.value)
            header.elements.each { HeaderElement ele ->
                ele.parameters.each { NameValuePair pair ->
                    buf.append('; ').append(pair.name).append('=').append(pair.value)
                }
            }
            buf.append('\n')
        }

    }


}
