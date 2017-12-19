package gov.nist.toolkit.utilities.html

import org.apache.http.HeaderElement
import org.apache.http.HttpHeaders
import org.apache.http.ProtocolVersion
import org.apache.http.RequestLine
import org.apache.http.StatusLine
import org.apache.http.message.BasicHeaderValueParser
import org.apache.http.message.BasicRequestLine
import org.apache.http.message.BasicStatusLine
import org.apache.http.message.HeaderValueParser

/**
 *
 */
class HeaderParser {
    static HeaderBlock parseHeaders(String rawHeaders) {
        HeaderBlock headerBlock = new HeaderBlock()
        headerBlock.requestLine = null
        HeaderValueParser parser = new BasicHeaderValueParser()
        rawHeaders.eachLine {String line ->
            line = strip(line)
            if (headerBlock.requestLine || headerBlock.statusLine) {
                if (line) {
                    def lineParts = line.split(':', 2)
                    assert lineParts.size() == 2
                    def name = lineParts[0]
                    HeaderElement[] headerElements = BasicHeaderValueParser.parseElements(lineParts[1], parser)
                    headerBlock.headers.add(new HeaderBlock.Header(name, headerElements))
                }
            }
            else {
                def firstLine = requestLineParser(line)
                if (firstLine instanceof RequestLine) headerBlock.requestLine = (RequestLine) firstLine
                else headerBlock.statusLine = (StatusLine) firstLine
            }
        }
        return headerBlock
    }

    private static String strip(String line) {
        line = line.trim()
        while (line.size() > 0 && (line.endsWith('\r') || line.endsWith('\n')) )
            line = line.substring(0, line.size() - 1)
        return line
    }


    // returns either RequestLine or StatusLine
    private static def requestLineParser(String line) {
        def method
        def uri
        def httpversion

        if (line.startsWith('HTTP')) {
            String[] parts = line.trim().split(' ', 3)
            assert parts.size() == 3
            def protocol = parts[0]
            def status = parts[1]
            def reason = parts[2]

            // HTTP/1.1
            def protoversion = protocol.split('/')
            assert protoversion.size() == 2
            def versions = protoversion[1].split('\\.')
            assert versions.size() == 2
            ProtocolVersion protocolVersion = new ProtocolVersion(protoversion[0], versions[0].toInteger(), versions[1].toInteger())
            BasicStatusLine statusLine = new BasicStatusLine(protocolVersion, status.toInteger(), reason)
            return statusLine
        } else {
            String[] parts = line.trim().split(' ')
            method = parts[0]
            uri = parts[1]
            // HTTP/1.1
            httpversion = parts[2]
            String[] parts2 = httpversion.split('/')
            assert parts2.size() == 2
            def protocol = parts2[0]
            String[] parts3 = parts2[1].split('\\.')
            assert parts3.size() == 2
            int major = parts3[0].toInteger()
            int minor = parts3[1].toInteger()

            ProtocolVersion protocolVersion = new ProtocolVersion(protocol, major, minor)

            return new BasicRequestLine(method, uri, protocolVersion)
        }
    }

    static String mimeType(HeaderBlock hdrs) {
        hdrs.get (HttpHeaders.CONTENT_TYPE)?.name
    }

}
