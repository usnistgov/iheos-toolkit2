package gov.nist.toolkit.results

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import org.hl7.fhir.dstu3.model.DocumentReference
import spock.lang.Specification

class ResourceToMetadataCollectionParserTest extends Specification {
    ResourceToMetadataCollectionParser parser = new ResourceToMetadataCollectionParser()
    DocumentReference documentReference

    def setup() {
        URL externalCacheMarker = getClass().getResource('/external_cache/external_cache.txt')
        if (externalCacheMarker == null) {
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }
        File externalCache = new File(externalCacheMarker.toURI().path).parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)



        String dr = this.class.getResource('/DocumentReference1.xml').text
        documentReference = ToolkitFhirContext.get().newXmlParser().parseResource(dr)
    }

    def 'test 1' () {
        when:
        parser.add(documentReference, null)

        then:
        parser.col.docEntries.size() == 1
        parser.col.docEntries[0].uniqueId == 'urn:oid:1.2.129.6.58.92.88336'
    }

}
