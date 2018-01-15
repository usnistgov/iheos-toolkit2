package gov.nist.toolkit.simcoresupport.proxy.util

import gov.nist.toolkit.simcommon.server.SimDb
import org.apache.http.*
/**
 *
 */
class ProxyLogger {
    SimDb simDb
    byte[] content

    ProxyLogger(SimDb simDb) {
        assert simDb
        this.simDb = simDb
    }

    def logRequestSourceAddress(String addr) {
        simDb.clientIpAddess = addr
    }

    def logRequest(HttpRequest request) {
        simDb.putRequestHeaderFile(asString(request).bytes)
    }

    def logRequestEntity(byte[] content) {
        this.content = content
        simDb.putRequestBodyFile(content);
    }

    def logResponse( HttpResponse response) {
        simDb.putResponseHeaderFile(asString(response).bytes)
    }

    def logResponseEntity(byte[] content) {
        this.content = content
        simDb.putResponseBody(content)
    }

     String asString(HttpResponse response) {
        StringBuilder buf = new StringBuilder()

        buf.append(response.statusLine.toString()).append('\r\n')
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
