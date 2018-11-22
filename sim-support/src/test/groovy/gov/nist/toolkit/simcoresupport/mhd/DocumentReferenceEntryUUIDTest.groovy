package gov.nist.toolkit.simcoresupport.mhd

import gov.nist.toolkit.fhir.server.resourceMgr.ResourceCacheMgr
import gov.nist.toolkit.installation.server.Installation
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
                <masterIdentifier>
                    <system value="urn:ietf:rfc:3986"/>
                    <value value="urn:oid:1.2.129.6.58.92.88336"/>
                </masterIdentifier>
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
        eo.@id.text().startsWith('urn:uuid:')  // one is added
    }

    def 'submitted with entryUUID'() {
        def docRefWithEntryUUID = '''
<DocumentReference>
                <masterIdentifier>
                    <system value="urn:ietf:rfc:3986"/>
                    <value value="urn:oid:1.2.129.6.58.92.88336"/>
                </masterIdentifier>
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
        Utils.DocumentReferenceToExtrinsicObject(gen, 'http://home.com/fhir/DocumentReference/1', docRefNonUUIDEntryUUID)

        then:
        thrown AssertionError
    }






}
