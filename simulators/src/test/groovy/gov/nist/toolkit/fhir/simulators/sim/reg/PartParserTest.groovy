package gov.nist.toolkit.fhir.simulators.sim.reg

import gov.nist.toolkit.fhir.simulators.proxy.util.ResponsePartParser
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.message.BasicHttpResponse
import spock.lang.Specification
/**
 *
 */
class PartParserTest extends Specification {
    def part = '''
--MIMEBoundary112233445566778899
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"
Content-Transfer-Encoding: binary
Content-ID: <doc0@ihexds.nist.gov>

<S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope"><S:Header/><S:Body><fault:Fault xmlns:fault="http://www.w3.org/2003/05/soap-envelope"><fault:Code><fault:Value>fault:Sender</fault:Value></fault:Code><fault:Reason><fault:Text xml:lang="en">Header/Format Validation errors reported by SoapMessageValidator

Expected [1]  Found [ 0]


Expected [1]  Found [ 0]


Expected [http://www.w3.org/2003/05/soap-envelope]  Found [ urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0]


Expected [Envelope]  Found [ RegistryObjectList]

Validate SOAP Header

Expected [Present]  Found [ Missing]


Expected [Found]  Found [ Missing]
</fault:Text></fault:Reason></fault:Fault></S:Body></S:Envelope>

--MIMEBoundary112233445566778899--
'''

    def 'test' () {
        when:
        BasicHttpResponse response = ResponsePartParser.parse(part)
        String content = Io.getStringFromInputStream(response.getEntity().content)

        then:
        content.startsWith('<S')
    }
}
