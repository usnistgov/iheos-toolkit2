package gov.nist.toolkit.testengine

import gov.nist.toolkit.testengine.support.ContentTypeParser
import spock.lang.Specification

class ContentTypeParserTest extends Specification {
    def expected = 'application/fhir+xml'

    def 'well formatted - isolated' () {
        when:
        def contentType = 'application/fhir+xml'

        then:
        expected == new ContentTypeParser(contentType).contentType
    }

    def 'well formatted - with charset' () {
        when:
        def contentType = 'application/fhir+xml; charset=utf-8'
        def parser = new ContentTypeParser(contentType)

        then:
        expected == parser.contentType
        'utf-8' == parser.parms['charset']
    }

    def 'leading space' () {
        when:
        def contentType = ' application/fhir+xml; charset=utf-8'
        def parser = new ContentTypeParser(contentType)

        then:
        expected == parser.contentType
        'utf-8' == parser.parms['charset']
    }
}
