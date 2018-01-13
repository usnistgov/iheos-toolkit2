package gov.nist.toolkit.simcoresupport.mhd

import gov.nist.toolkit.fhir.server.resourceMgr.ResourceCacheMgr
import gov.nist.toolkit.installation.Installation
import spock.lang.Specification

class DocumentReferenceEntryUUIDTest extends Specification {
    MhdGenerator gen = new MhdGenerator(null, new ResourceCacheMgr())

    def setup() {
        Installation.setTestRunning(true)
    }

    def 'submitted without entryUUID'() {
        given:
        def docRefNoEntryUUID = '''
<DocumentReference>
    <content>
        <attachment>
            <contentType value="text/plain"/>
            <url value="http://localhost:9556/svc/fhir/Binary/1e404af3-077f-4bee-b7a6-a9be97e1ce32"/>
        </attachment>
    </content>
</DocumentReference>
'''

        when:
        def eoString = Utils.DocumentReferenceToExtrinsicObject(gen, 'http://home.com/fhir/DocumentReference/1', docRefNoEntryUUID)
        def eo = new XmlSlurper().parseText(eoString)

        then:
        eo.@id == 'SymbolicId1'
    }

    def 'submitted with entryUUID'() {
        def docRefWithEntryUUID = '''
<DocumentReference>
    <identifier>
        <system value="urn:ietf:rfc:3986"/>
        <use value="official"/>
        <value value="urn:uuid:1e404af3-077f-4bee-b7a6-a9be97e1ce34"/>
    </identifier>
    <content>
        <attachment>
            <contentType value="text/plain"/>
            <url value="http://localhost:9556/svc/fhir/Binary/1e404af3-077f-4bee-b7a6-a9be97e1ce32"/>
        </attachment>
    </content>
</DocumentReference>
'''

        when:
        def eoString = Utils.DocumentReferenceToExtrinsicObject(gen, 'http://home.com/fhir/DocumentReference/1', docRefWithEntryUUID)
        def eo = new XmlSlurper().parseText(eoString)

        then:
        eo.@id == 'urn:uuid:1e404af3-077f-4bee-b7a6-a9be97e1ce34'
    }

    def 'submitted with non-UUID entryUUID'() {
        given:
        def docRefNonUUIDEntryUUID = '''
<DocumentReference>
    <identifier>
        <system value="urn:ietf:rfc:3986"/>
        <use value="official"/>
        <value value="1e404af3-077f-4bee-b7a6-a9be97e1ce34"/>
    </identifier>
    <content>
        <attachment>
            <contentType value="text/plain"/>
            <url value="http://localhost:9556/svc/fhir/Binary/1e404af3-077f-4bee-b7a6-a9be97e1ce32"/>
        </attachment>
    </content>
</DocumentReference>
'''

        when:
        def eoString = Utils.DocumentReferenceToExtrinsicObject(gen, 'http://home.com/fhir/DocumentReference/1', docRefNonUUIDEntryUUID)
        def eo = new XmlSlurper().parseText(eoString)

        then:
        thrown AssertionError
    }






}
