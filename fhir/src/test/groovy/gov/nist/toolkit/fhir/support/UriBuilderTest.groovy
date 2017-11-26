package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.fhir.utility.UriBuilder
import spock.lang.Specification

class UriBuilderTest extends Specification {

    def 'single arg'() {
        expect:
        new URI('http://example.com/foo?x=y%7cz') != 'http://example.com/foo?x=y|z'
    }

    def 'multi arg'() {
        URI uri = new URI('http', '/example.com/foo?x=y|z', null)
        expect:
        uri != 'http://example.com/foo?x=y|z'
        uri.query == 'x=y|z'
    }

    def 'parsed multi arg'() {
        def ref = 'http://example.com/foo?x=y|z'
        expect:
        UriBuilder.build(ref).toString() == 'http://example.com/foo?x=y%7Cz'
    }

    def 'parsed fragment'() {
        def ref = 'http://example.com/foo?x=y|z#yy'
        expect:
        UriBuilder.build(ref).toString() == 'http://example.com/foo?x=y%7Cz%23yy'
    }

    def 'fragment only'() {
        def ref = '#yy'
        expect:
        UriBuilder.build(ref).toString() == '#%23yy'
    }

}
