package gov.nist.toolkit.http

import gov.nist.toolkit.errorrecording.TextErrorRecorder
import spock.lang.Specification

/**
 *
 */
class MultipartParserBaTest extends Specification {
    Map<String, List<String>> headers = new HashMap<>()
    byte[] body

    def buildTestData(String content) {
        headers.clear()
        String boundary = 'Boundary'
        List<String> contentTypeHeader = ['multipart/related; boundary=' + boundary + '; type="application/xop+xml"']
        String contentType = 'Content-Type'
        headers.put(contentType, contentTypeHeader)
        StringBuilder buf = new StringBuilder()
        String CRLF = '\r\n'
        buf << CRLF << '--' << boundary
        buf << CRLF << contentType << ': ' << 'text/plain'
        buf << CRLF << 'Content-ID: <foo#home>'
        buf << CRLF
        buf << CRLF << content
        buf << CRLF << '--' << boundary << '--'
        buf << CRLF
        body = buf.toString().bytes

        println headers
        println new String(body)
    }

    def 'parse'() {
        setup:
        String orig = 'Hello World!'
        buildTestData(orig)
        HttpParserBa hParser = new HttpParserBa(headers, body)
        TextErrorRecorder errorRecorder = new TextErrorRecorder()

        when:
        MultipartParserBa mParser = new MultipartParserBa(hParser, errorRecorder, true)
        PartBa part = mParser.getPart(0)
        byte[] body = part.body
        println errorRecorder.toString()
        String bodyAsString = new String(body)

        then:
//        orig.size() == bodyAsString.size()
        'Hello World!' == bodyAsString
    }

    def 'parse with extra space'() {
        setup:
        String orig = 'Hello World!\n\n\n\n\n\n\n'
        buildTestData(orig)
        HttpParserBa hParser = new HttpParserBa(headers, body)
        TextErrorRecorder errorRecorder = new TextErrorRecorder()

        when:
        MultipartParserBa mParser = new MultipartParserBa(hParser, errorRecorder, true)
        PartBa part = mParser.getPart(0)
        byte[] body = part.body
        println errorRecorder.toString()

        then:
        orig == new String(body)
    }

}
