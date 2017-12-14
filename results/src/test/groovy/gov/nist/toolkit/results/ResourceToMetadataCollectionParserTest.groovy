package gov.nist.toolkit.results

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import org.hl7.fhir.dstu3.model.DocumentReference
import spock.lang.Specification

class ResourceToMetadataCollectionParserTest extends Specification {
    ResourceToMetadataCollectionParser parser = new ResourceToMetadataCollectionParser()
    DocumentReference documentReference

    def setup() {
        String dr = this.class.getResource('/DocumentReference1.xml').text
        documentReference = ToolkitFhirContext.get().newXmlParser().parseResource(dr)
    }

    def 'test 1' () {
        when:
        parser.add(documentReference)

        then:
        parser.col.docEntries.size() == 1
        parser.col.docEntries[0].uniqueId == '1.2.129.6.58.92.88336'
    }

}
