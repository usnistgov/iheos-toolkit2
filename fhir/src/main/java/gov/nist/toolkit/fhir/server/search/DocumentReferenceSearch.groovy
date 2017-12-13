package gov.nist.toolkit.fhir.server.search

import gov.nist.toolkit.fhir.shared.searchModels.IdentifierSM
import gov.nist.toolkit.fhir.shared.searchModels.LogicalIdSM
import gov.nist.toolkit.fhir.shared.searchModels.ResourceType

class DocumentReferenceSearch extends BasicSearch {
    IdentifierSM patientIdentifier = null
    String patientUrl = null

    DocumentReferenceSearch(FhirBase fhirBase, LogicalIdSM logicalId) {
        super(fhirBase, ResourceType.DocumentReference, logicalId)
    }

    def prepare() {
        if (patientIdentifier && !patientUrl) {
            def params = ["identifier=${patientIdentifier.system}|${patientIdentifier.code}"]
            def resources = fhirBase.search(resourceType, params)
        }
    }

    @Override
    String asQueryString() {
        if (!valid) return null
        if (read) return asReadString()
    }
}
