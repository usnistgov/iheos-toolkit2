package gov.nist.toolkit.fhir.simulators.proxy.util

import org.apache.http.ProtocolVersion
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine

/**
 *
 */
class ResponsePartParser {
    static BasicHttpResponse parse(String x) {
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion('http', 1, 1), 200, '')) // statusLine is bogus
        boolean inheader = false
        boolean inbody = false
        ContentType contentType = null
        StringBuilder buf = new StringBuilder()
        x.trim().eachLine { String line ->
            line = line.trim()
            if (line.size() == 0) {if (inheader) inheader = false; return }
            if (line.startsWith('--')) {inheader = true; return}
            if (inheader) {
                def parts = line.split(':')
                if (parts[0].equalsIgnoreCase('content-type')) {
                    String ctype = parts[1].split(';')[0]
                    contentType = ContentType.create(ctype)
                }
                response.addHeader(parts[0], parts[1])
            } else {
                buf.append(line).append('\n')
            }
        }
        if (buf.size())
            response.setEntity(new StringEntity(buf.toString(), contentType))
        return response
    }
}
