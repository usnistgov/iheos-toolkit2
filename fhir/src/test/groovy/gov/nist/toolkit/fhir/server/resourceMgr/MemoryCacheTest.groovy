package gov.nist.toolkit.fhir.server.resourceMgr

import gov.nist.toolkit.installation.Installation
import org.hl7.fhir.dstu3.model.DocumentReference
import spock.lang.Shared
import spock.lang.Specification

class MemoryCacheTest extends Specification {
    @Shared ResourceCacheMgr cacheMgr = ResourceCacheMgr.instance()

    def setup() {
        Installation.setTestRunning(true)
    }

    def 'simple retrieve'() {
        when:
        DocumentReference dr = new DocumentReference()
        DocumentReference dr2 = new DocumentReference()
        URI uri = new URI('http://home.com/foo/DocumentReference/1')
        cacheMgr.addMemoryCacheElement(uri, dr)

        then:
        cacheMgr.getResource(uri) == dr
        cacheMgr.getResource(uri) != dr2
    }
}
