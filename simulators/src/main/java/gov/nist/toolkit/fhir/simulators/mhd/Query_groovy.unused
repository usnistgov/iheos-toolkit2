package gov.nist.toolkit.fhir.simulators.mhd

import org.apache.commons.lang.text.StrSubstitutor

/**
 *
 */
class Query {

    static def wrapper = '''
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
      <wsa:To soapenv:mustUnderstand="true">THEENDPOINT</wsa:To>
      <wsa:MessageID soapenv:mustUnderstand="true">urn:uuid:B163C7B266257EAA091504010552642</wsa:MessageID>
      <wsa:Action soapenv:mustUnderstand="true">THEACTION</wsa:Action>
   </soapenv:Header>
   <soapenv:Body>
'''
    static def wrapper1 = '''
          </query:AdhocQueryRequest>
'''
    static def wrapper2 = '''
   </soapenv:Body>
</soapenv:Envelope>
'''

    static String metadataInSoapWrapper(String endpoint, String action, String query) {
        return wrapper.replace('THEENDPOINT', endpoint).
                replace('THEACTION', action) + query + wrapper2
    }

    static String header(String theService, String theHost, String thePort, String theAction) {
        String headerTemplate = new Query().class.getResource('/templates/simple_header.txt').text
        def map = [theService: theService, theHost: theHost, thePort: thePort, theAction:theAction]
        new StrSubstitutor(map).replace(headerTemplate)
    }
}
