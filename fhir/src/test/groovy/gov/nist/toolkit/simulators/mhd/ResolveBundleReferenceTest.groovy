package gov.nist.toolkit.simulators.mhd

import gov.nist.toolkit.installation.ResourceCacheMgr
import gov.nist.toolkit.installation.ResourceMgr
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import org.hl7.fhir.dstu3.model.DocumentReference
import spock.lang.Shared
import spock.lang.Specification

/**
 * Test the resolution of references in bundles
 *
 * entry.fullUrl is absolute URL for reference or UUID/OID
 * When looking at reference, entry.fullUrl is documented as the base URL
 */
class ResolveBundleReferenceTest extends Specification {
    @Shared MhdGenerator u = new MhdGenerator(new SimProxyBase(), new ResourceCacheMgr())

    def 'absolute absolute' () {
        given:
        def full = 'http://example.com/fhir/DocumentReference/1'
        def ref = 'http://example.com/fhir/Patient/3'

        when:
        def resolve = ResourceMgr.resolveUrl(full, ref)

        then:
        resolve == ref
    }

    def 'absolute relative' () {
        given:
        def full = 'http://example.com/fhir/DocumentReference/1'
        def ref = 'Patient/3'

        when:
        def resolve = u.rMgr.resolveUrl(full, ref)

        then:
        resolve == 'http://example.com/fhir/Patient/3'
    }

    def 'relative absolute' () {
        given:
        def full = 'urn:oid:1.2.5.6'
        def ref = 'http://example.com/fhir/Patient/3'

        when:
        def resolve = u.rMgr.resolveUrl(full, ref)

        then:
        resolve == ref
    }

    def 'relative relative' () {
        given:
        def full = 'urn:oid:1.2.5.6'
        def ref = 'Patient/3'

        when:
        def resolve = u.rMgr.resolveUrl(full, ref)

        then:
        resolve == ref
    }

    class Toy {
        def url

        Toy(url) { this.url = url }
    }

    def 'resolve reference' () {
        given:
        ResourceMgr rMgr = new ResourceMgr()
        def fullUrl = 'http://example.com/fhir/Collection/6'

        when:
        def bob = new Toy('bob')
        rMgr.addResource('urn:oid:1.2.3', bob)
        def alice = new Toy('alice')
        rMgr.addResource('urn:oid:1.2.4', alice)
        def internal = new Toy('internal')
        rMgr.addResource('http://example.com/fhir/Person/1', internal)

        then:
//        rMgr.resolveReference(fullUrl, 'urn:oid:1.2.3')[1] == bob  // local ref
//        rMgr.resolveReference(fullUrl, 'urn:oid:1.2.4')[1] == alice  // different local ref
//        rMgr.resolveReference(fullUrl, 'http://example.com/fhir/Person/1')[1] == internal  // in bundle
        rMgr.resolveReference(fullUrl, 'Person/1', true, false)[1] == internal    // should get 'adopted' by full url base
        rMgr.resolveReference(fullUrl, 'http://example.com/fhir/Person/2', true, false)[1] == null  // not available from bundle
    }

    def 'get all of type' () {
        given:
        def dr = new DocumentReference()
        u.rMgr.addResource('url', dr)

        when:
        def all = u.rMgr.getAllOfType('DocumentReference')

        then:
        all == [['url', dr]]
    }
}
