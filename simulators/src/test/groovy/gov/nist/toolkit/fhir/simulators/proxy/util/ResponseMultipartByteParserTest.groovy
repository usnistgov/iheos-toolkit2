package gov.nist.toolkit.fhir.simulators.proxy.util

import spock.lang.Specification

class ResponseMultipartByteParserTest extends Specification {

    def 'startsWith 1' () {
        expect:
        MultipartParser2.startsWith('foobar'.bytes, 0, 'foo'.bytes)
    }

    def 'startsWith 2' () {
        expect:
        MultipartParser2.startsWith('foobar'.bytes, 3, 'bar'.bytes)
    }

    def 'startsWith 3' () {
        expect:
        !MultipartParser2.startsWith('foobar'.bytes, 2, 'bar'.bytes)
    }

    def 'startsWith 4' () {
        expect:
        !MultipartParser2.startsWith('foobar'.bytes, 5, 'bar'.bytes)
    }

    def 'startsWith 5'() {
        expect:
        !MultipartParser2.startsWith('foobar'.bytes, 6, 'bar'.bytes)
    }

    def 'startsWith 6'() {
        expect:
        !MultipartParser2.startsWith('foobar'.bytes, 6, ''.bytes)
    }

    def 'startsWith 7'() {
        expect:
        !MultipartParser2.startsWith('foobar'.bytes, 3, 'barx'.bytes)
    }

    def 'startsWith 8'() {
        expect:
        !MultipartParser2.startsWith('foobar'.bytes, 0, 'bb'.bytes)
    }

    def 'upto 1' () {
        expect:
        MultipartParser2.upto('foobar'.bytes, 0, 'bar'.bytes) == 'foo'.bytes
    }

    def 'upto 2' () {
        expect:
        MultipartParser2.upto('foobar'.bytes, 0, 'xx'.bytes) == ''.bytes
    }

    def 'upto 3' () {
        expect:
        MultipartParser2.upto(''.bytes, 0, 'xx'.bytes) == ''.bytes
    }

    def 'upto 4' () {
        expect:
        MultipartParser2.upto('barx'.bytes, 0, 'xx'.bytes) == ''.bytes
    }

    def 'upto 5' () {
        expect:
        MultipartParser2.upto('barx'.bytes, 5, 'xx'.bytes) == ''.bytes
    }

    def 'find 1' () {
        expect:
        MultipartParser2.find('foobar'.bytes, 0, 'foo'.bytes) == 0
    }

    def 'find 2' () {
        expect:
        MultipartParser2.find('foobar'.bytes, 0, 'oo'.bytes) == 1
    }

    def 'find 3' () {
        expect:
        MultipartParser2.find('foobar'.bytes, 1, 'ba'.bytes) == 3
    }

    def 'find 4' () {
        expect:
        MultipartParser2.find('foobar'.bytes, 0, 'bar'.bytes) == 3
    }

    def 'strip 1' () {
        expect:
        MultipartParser2.strip('foobar'.bytes) == 'foobar'.bytes
    }

    def 'strip 2' () {
        expect:
        MultipartParser2.strip('\rfoobar'.bytes) == 'foobar'.bytes
    }

    def 'strip 3' () {
        expect:
        MultipartParser2.strip('\r\nfoobar'.bytes) == 'foobar'.bytes
    }

    def 'strip 4' () {
        expect:
        MultipartParser2.strip('foobar\r'.bytes) == 'foobar'.bytes
    }

    def 'strip 5' () {
        expect:
        MultipartParser2.strip('foobar\r\n'.bytes) == 'foobar'.bytes
    }



    def 'find 5' () {
        expect:
        MultipartParser2.find('foobar'.bytes, 0, 'barx'.bytes) == -1
    }

    def 'lineStartsAt 1' () {
        expect:
        MultipartParser2.lineStartingAt('foobar\n'.bytes, 0) == 'foobar'.bytes
    }

    def 'lineStartsAt 2' () {
        expect:
        MultipartParser2.lineStartingAt('foobar\nabmceek'.bytes, 0) == 'foobar'.bytes
    }

    def 'lineStartsAt 3' () {
        expect:
        MultipartParser2.lineStartingAt('foobar'.bytes, 0) == ''.bytes
    }

    def 'boundary includes preceding newline'() {
        when:
        List<BinaryPartSpec> parts = MultipartParser2.parse(msg.bytes)

        then:
        parts.size() == 2
        new String(parts[0].content) == '<Envelope/>\n'
        new String(parts[1].content) == 'asdasdasdasdasd'
    }

    def 'boundary does not include CR'() {
        when:
        List<BinaryPartSpec> parts = MultipartParser2.parse(msg1.bytes)

        then:
        parts.size() == 2
        new String(parts[0].content) == '<Envelope/>\n'
        new String(parts[1].content) == 'asdasdasdasdasd'
    }

    def 'parse2'() {
        when:
        List<BinaryPartSpec> parts = MultipartParser2.parse(msg2.bytes)
        def size = '''asdasdasdasdasd
'''.size()

        then:
        parts.size() == 2
        new String(parts[0].content).startsWith('<S:Envelope')
        new String(parts[1].content).startsWith('asdasd')
        new String(parts[1].content).size() == size
    }



    def msg = '''\r
--MIMEBoundary112233445566778899\r
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"\r
Content-Transfer-Encoding: binary\r
Content-ID: <doc0@ihexds.nist.gov>\r
\r
<Envelope/>\r
\r
--MIMEBoundary112233445566778899\r
Content-Type: text/plain\r
Content-Transfer-Encoding: binary\r
Content-ID: <doc1@ihexds.nist.gov>\r
\r
asdasdasdasdasd\r
--MIMEBoundary112233445566778899--\r
'''

    def msg1 = '''
--MIMEBoundary112233445566778899
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"
Content-Transfer-Encoding: binary
Content-ID: <doc0@ihexds.nist.gov>

<Envelope/>

--MIMEBoundary112233445566778899
Content-Type: text/plain
Content-Transfer-Encoding: binary
Content-ID: <doc1@ihexds.nist.gov>

asdasdasdasdasd
--MIMEBoundary112233445566778899--
'''

    def msg2 = '''\r
--MIMEBoundary112233445566778899\r
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"\r
Content-Transfer-Encoding: binary\r
Content-ID: <doc0@ihexds.nist.gov>\r
\r
<S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope"><S:Header><wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope" s:mustUnderstand="1">urn:ihe:iti:2007:RetrieveDocumentSetResponse</wsa:Action><wsa:RelatesTo xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:uuid:B163C7B266257EAA091504010552642</wsa:RelatesTo></S:Header><S:Body><xdsb:RetrieveDocumentSetResponse xmlns:xdsb="urn:ihe:iti:xds-b:2007"><rs:RegistryResponse xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0" status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success"/><xdsb:DocumentResponse><xdsb:RepositoryUniqueId>1.1.4567332.1.3</xdsb:RepositoryUniqueId><xdsb:DocumentUniqueId>1.2.42.20171130082535.2</xdsb:DocumentUniqueId><xdsb:mimeType>text/plain</xdsb:mimeType><xdsb:Document><xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:doc1@ihexds.nist.gov"/></xdsb:Document></xdsb:DocumentResponse></xdsb:RetrieveDocumentSetResponse></S:Body></S:Envelope>\r
\r
--MIMEBoundary112233445566778899\r
Content-Type: text/plain\r
Content-Transfer-Encoding: binary\r
Content-ID: <doc1@ihexds.nist.gov>\r
\r
asdasdasdasdasd\r
\r
--MIMEBoundary112233445566778899--\r
'''
}
