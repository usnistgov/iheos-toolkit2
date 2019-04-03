package gov.nist.toolkit.fhir.server.utility

import spock.lang.Specification

class IsolateContentTypeTest extends Specification {

    def 'simple type' () {
        when:
        def type = 'application/fhir+json'
        def (contentType, error) = FhirClient.isolateContentType(type, type)

        then:
        contentType == 'application/fhir+json'
        !error
    }

    def 'simple type with spaces' () {
        when:
        def type = '  application/fhir+json  '
        def (contentType, error) = FhirClient.isolateContentType(type, type)

        then:
        contentType == 'application/fhir+json'
        !error
    }

    def 'simple type with charset' () {
        when:
        def expected = 'application/fhir+json'
        def type = 'application/fhir+json;charset=UTF-8'
        def (contentType, error) = FhirClient.isolateContentType(type, expected)

        then:
        contentType == expected
        !error
    }

    def 'simple type with charset and spaces' () {
        when:
        def expected = 'application/fhir+json'
        def type = ' application/fhir+json ; charset=UTF-8 '
        def (contentType, error) = FhirClient.isolateContentType(type, expected)

        then:
        contentType == expected
        !error
    }

    def 'bad type' () {
        when:
        def expected = 'application/fhir+json'
        def type = ' application/fhir+jso ; charset=UTF-8 '
        def (contentType, error) = FhirClient.isolateContentType(type, expected)

        then:
        contentType != expected
        error.contains('Requested')
    }
}
