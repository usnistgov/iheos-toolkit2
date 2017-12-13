package gov.nist.toolkit.fhir.server.search

import gov.nist.toolkit.fhir.shared.searchModels.IdentifierSM
import gov.nist.toolkit.fhir.shared.searchModels.LogicalIdSM
import gov.nist.toolkit.fhir.shared.searchModels.ResourceType
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.instance.model.api.IBaseResource

class DocumentReferenceSearch extends BasicSearch {
    IdentifierSM patientIdentifier = null
    URI patientUri = null

    DocumentReferenceSearch(FhirBase fhirBase, LogicalIdSM logicalId) {
        super(fhirBase, ResourceType.DocumentReference, logicalId)
    }

    /**
     *
     * @return success
     */
    boolean prepare() {
        if (patientIdentifier && !patientUri) {
            List<String> params = ["identifier=${patientIdentifier.system}|${patientIdentifier.code}"]
            Map<URI, IBaseResource> resources = fhirBase.search(ResourceType.Patient, params)
            if (!resources) return false
            assert resources.values()[0] instanceof Patient
            patientUri = resources.keySet()[0]
        }
        return true
    }

    @Override
    String asQueryString() {
        if (!valid) return null
        if (read) return asReadString()

        prepare()

        List<String> params = []
        if (patientIdentifier) {
            params.add("subject=${patientUri.toString()}")
        }

        fhirBase.buildURL(resourceType, params).toString()
    }
}
