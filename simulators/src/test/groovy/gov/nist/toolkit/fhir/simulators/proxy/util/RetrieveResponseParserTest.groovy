package gov.nist.toolkit.fhir.simulators.proxy.util

import spock.lang.Specification

class RetrieveResponseParserTest extends Specification {

    def 'test'() {
        when:
        List<RetrieveResponseParser.RetrieveContent> contents = new RetrieveResponseParser().parse(msg)

        then:
        contents.size() == 1
        contents[0].content == 'asdasdasdasdasd'.bytes
    }



    def msg = '''\r
--MIMEBoundary112233445566778899\r
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"\r
Content-Transfer-Encoding: binary\r
Content-ID: <doc0@ihexds.nist.gov>\r
\r
<S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope">\r
 <S:Header>\r
   <wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope" s:mustUnderstand="1">urn:ihe:iti:2007:RetrieveDocumentSetResponse</wsa:Action>\r
   <wsa:RelatesTo xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:uuid:B163C7B266257EAA091504010552642</wsa:RelatesTo>\r
 </S:Header>\r
 <S:Body>\r
 <xdsb:RetrieveDocumentSetResponse xmlns:xdsb="urn:ihe:iti:xds-b:2007">\r
   <rs:RegistryResponse xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0" status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success"/>\r
   <xdsb:DocumentResponse>\r
       <xdsb:RepositoryUniqueId>1.1.4567332.1.3</xdsb:RepositoryUniqueId>\r
       <xdsb:DocumentUniqueId>1.2.42.20171130082535.2</xdsb:DocumentUniqueId>\r
       <xdsb:mimeType>text/plain</xdsb:mimeType>\r
       <xdsb:Document>\r
          <xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:doc1@ihexds.nist.gov"/>\r
       </xdsb:Document>\r
     </xdsb:DocumentResponse>\r
   </xdsb:RetrieveDocumentSetResponse>\r
 </S:Body>\r
</S:Envelope>\r
\r
--MIMEBoundary112233445566778899\r
Content-Type: text/plain\r
Content-Transfer-Encoding: binary\r
Content-ID: <doc1@ihexds.nist.gov>\r
\r
asdasdasdasdasd\r
--MIMEBoundary112233445566778899--\r
'''
}
