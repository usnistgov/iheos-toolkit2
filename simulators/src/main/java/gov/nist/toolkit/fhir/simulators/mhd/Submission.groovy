package gov.nist.toolkit.fhir.simulators.mhd

/**
 *
 */
class Submission {
// TODO insert real To address
    def header = '''
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
      <wsa:To soapenv:mustUnderstand="true">http://localhost:8888/sim/default__rr/rep/prb</wsa:To>
      <wsa:MessageID soapenv:mustUnderstand="true">urn:uuid:B163C7B266257EAA091504010552642</wsa:MessageID>
      <wsa:Action soapenv:mustUnderstand="true">urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b</wsa:Action>
   </soapenv:Header>
   <soapenv:Body>
      <xdsb:ProvideAndRegisterDocumentSetRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
          <lcm:SubmitObjectsRequest xmlns:lcm="urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0">
'''
    def trailer1 = '''
          </lcm:SubmitObjectsRequest>
'''
    def trailer2 = '''
      </xdsb:ProvideAndRegisterDocumentSetRequest>
   </soapenv:Body>
</soapenv:Envelope>
'''

    String registryObjectList
    String documentDefinitions
    List<Attachment> attachments = []
    String contentId   // for metadata

    String metadataInSoapWrapper() {
        return header + registryObjectList + trailer1 + documentDefinitions + trailer2
    }
}
