package gov.nist.toolkit.fhir.simulators.proxy.util

import spock.lang.Specification

class ResponseMultipartParserTest extends Specification {

    def 'parse'() {
        when:
        List<BinaryPartSpec> parts = MultipartParser2.parse(msg)
        def size = '''asdasdasdasdasd
'''.size()

        then:
        parts.size() == 2
        new String(parts[0].content).startsWith('<S:Envelope')
        new String(parts[1].content).startsWith('asdasd')
        new String(parts[1].content).size() == size
    }

    def 'parse2'() {
        when:
        List<BinaryPartSpec> parts = MultipartParser2.parse(msg2)
        def size = '''asdasdasdasdasd

'''.size()

        then:
        parts.size() == 2
        new String(parts[0].content).startsWith('<S:Envelope')
        new String(parts[1].content).startsWith('asdasd')
        new String(parts[1].content).size() == size
    }



    def msg = '''
--MIMEBoundary112233445566778899
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"
Content-Transfer-Encoding: binary
Content-ID: <doc0@ihexds.nist.gov>

<S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope"><S:Header><wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope" s:mustUnderstand="1">urn:ihe:iti:2007:RetrieveDocumentSetResponse</wsa:Action><wsa:RelatesTo xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:uuid:B163C7B266257EAA091504010552642</wsa:RelatesTo></S:Header><S:Body><xdsb:RetrieveDocumentSetResponse xmlns:xdsb="urn:ihe:iti:xds-b:2007"><rs:RegistryResponse xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0" status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success"/><xdsb:DocumentResponse><xdsb:RepositoryUniqueId>1.1.4567332.1.3</xdsb:RepositoryUniqueId><xdsb:DocumentUniqueId>1.2.42.20171130082535.2</xdsb:DocumentUniqueId><xdsb:mimeType>text/plain</xdsb:mimeType><xdsb:Document><xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:doc1@ihexds.nist.gov"/></xdsb:Document></xdsb:DocumentResponse></xdsb:RetrieveDocumentSetResponse></S:Body></S:Envelope>

--MIMEBoundary112233445566778899
Content-Type: text/plain
Content-Transfer-Encoding: binary
Content-ID: <doc1@ihexds.nist.gov>

asdasdasdasdasd
--MIMEBoundary112233445566778899--
'''

    def msg2 = '''
--MIMEBoundary112233445566778899
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"
Content-Transfer-Encoding: binary
Content-ID: <doc0@ihexds.nist.gov>

<S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope"><S:Header><wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope" s:mustUnderstand="1">urn:ihe:iti:2007:RetrieveDocumentSetResponse</wsa:Action><wsa:RelatesTo xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:uuid:B163C7B266257EAA091504010552642</wsa:RelatesTo></S:Header><S:Body><xdsb:RetrieveDocumentSetResponse xmlns:xdsb="urn:ihe:iti:xds-b:2007"><rs:RegistryResponse xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0" status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success"/><xdsb:DocumentResponse><xdsb:RepositoryUniqueId>1.1.4567332.1.3</xdsb:RepositoryUniqueId><xdsb:DocumentUniqueId>1.2.42.20171130082535.2</xdsb:DocumentUniqueId><xdsb:mimeType>text/plain</xdsb:mimeType><xdsb:Document><xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:doc1@ihexds.nist.gov"/></xdsb:Document></xdsb:DocumentResponse></xdsb:RetrieveDocumentSetResponse></S:Body></S:Envelope>

--MIMEBoundary112233445566778899
Content-Type: text/plain
Content-Transfer-Encoding: binary
Content-ID: <doc1@ihexds.nist.gov>

asdasdasdasdasd

--MIMEBoundary112233445566778899--
'''
}
