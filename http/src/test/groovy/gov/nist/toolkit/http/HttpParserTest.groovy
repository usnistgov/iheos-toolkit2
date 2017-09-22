package gov.nist.toolkit.http

import spock.lang.Specification

/**
 *
 */
class HttpParserTest extends Specification {

    def 'part test' () {
        when:
        def input = 'POST /sim/default__rr/reg/rb HTTP/1.1\r\nContent-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"\r\nContent-Transfer-Encoding: binary\r\nContent-ID: <0.5ec4cc776323e8f60dac0b09ac0be1e8abdad40b2b718f3c@apache.org>\r\n\r\n<foo/>\r\n'
        HttpParser parser = new HttpParser(input.bytes)
        HttpMessage message = parser.getHttpMessage()
        List<HttpMessage.Header> headers = message.headers

        then:
//        headers[0] == 'POST /sim/default__rr/reg/rb HTTP/1.1'
        headers[0] == 'Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"'
        headers[1] == 'Content-Transfer-Encoding: binary'
        headers[2] == 'Content-ID: <0.5ec4cc776323e8f60dac0b09ac0be1e8abdad40b2b718f3c@apache.org>'
    }
}
