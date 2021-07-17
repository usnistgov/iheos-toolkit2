package gov.nist.toolkit.fhir.search

import gov.nist.toolkit.fhir.server.search.BasicSearch
import gov.nist.toolkit.fhir.server.search.DocumentReferenceSearch
import gov.nist.toolkit.fhir.server.search.FhirBase
import gov.nist.toolkit.fhir.server.search.SearchGenerator
import gov.nist.toolkit.fhir.shared.searchModels.LogicalIdSM
import gov.nist.toolkit.fhir.shared.searchModels.ResourceType
import gov.nist.toolkit.fhir.shared.searchModels.SearchModel
import spock.lang.Specification

class SearchGeneratorTest extends Specification {
    SearchGenerator gen = new SearchGenerator()
    SearchModel model = new SearchModel()

    def 'generate search builder'() {
        when:
        model.resourceType = ResourceType.DocumentReference
        def builder = gen.getSearchBuilder(new FhirBase('http://example.com/fhir'), model)

        then:
        builder instanceof DocumentReferenceSearch
    }

    def 'logical id'() {
        when:
        model.resourceType = ResourceType.DocumentReference
        model.logicalId = new LogicalIdSM('1')
        BasicSearch builder = gen.getSearchBuilder(new FhirBase('http://example.com/fhir'), model)

        then:
        builder.asQueryString() == '/DocumentReference/1'
    }
}
