package gov.nist.toolkit.simProxy

import gov.nist.toolkit.simProxy.server.util.HttpHeaders
import spock.lang.Specification

/**
 *
 */
class HttpHeadersTest extends Specification {
    def data = '''
content-length: 3961\r
content-type: application/fhir+json\r
host: localhost:8889\r
connection: Keep-Alive\r
user-agent: Apache-HttpClient/4.5.2 (Java/1.8.0_73)\r
accept-encoding: gzip,deflate\r
'''

    def 'test' () {
        when:
        HttpHeaders headers = new HttpHeaders(data)

        then:
        headers.headers.size() == 7
    }
}
