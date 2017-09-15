package gov.nist.toolkit.proxy

import org.apache.http.Header
import org.apache.http.HeaderElement
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHeaderValueParser
import spock.lang.Specification
/**
 * Created by bill on 9/13/17.
 */
class BasicHeaderTest extends Specification {

    def 'simple request' () {
        when:
        Header hdr = new BasicHeader('NickName', 'sam')

        then:
        hdr.getValue() == 'sam'
    }

    def 'complex request' () {
        when:
        def value = 'multipart/related; boundary="MIMEBoundary_494859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f"; type="application/xop+xml"; start="<1.694859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f@apache.org>"; start-info="application/soap+xml"; action="TheAction"'
        Header hdr = new BasicHeader('Content-Type', value)

        then:
        hdr.getValue() == value
        hdr.elements[0].name == 'multipart/related'
        hdr.elements[0].value == null
        hdr.elements.size() == 1

        when:
        HeaderElement[] eles = BasicHeaderValueParser.parseElements(value, null)

        then:
        eles.size() == 1

        when:
        HeaderElement ele = eles[0]

        then:
        ele.name == 'multipart/related'
        ele.parameters.size() == 5
        ele.parameters[0].name == 'boundary'
        ele.parameters[0].value == 'MIMEBoundary_494859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f'
    }
}
