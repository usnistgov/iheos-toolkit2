package gov.nist.toolkit.registrymsg.registry

import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement
import spock.lang.Specification

/**
 *
 */
class AdhocQueryRequestParserTest extends Specification {

    def query = '''
<query:AdhocQueryRequest xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
      xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0">
   <query:ResponseOption returnComposedObjects="true"
         returnType="ObjectRef"/>
   <AdhocQuery id="urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d">
      <Slot name="$XDSDocumentEntryPatientId">
         <ValueList>
            <Value>'5557247003^^^&amp;1.2.16.840.1.113883.2.1.4.1&amp;ISO'</Value>
         </ValueList>
      </Slot>
      <Slot name="$XDSDocumentEntryStatus">
         <ValueList>
            <Value>('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated')</Value>
         </ValueList>
      </Slot>
   </AdhocQuery>
</query:AdhocQueryRequest>
'''

    def 'extract patient id from query request'() {
        when:
        OMElement queryEle = Util.parse_xml(query)
        AdhocQueryRequestParser parser = new AdhocQueryRequestParser(queryEle)
        AdhocQueryRequest request = parser.getAdhocQueryRequest()

        then:
        request.patientId == '5557247003^^^&1.2.16.840.1.113883.2.1.4.1&ISO'

    }
}
