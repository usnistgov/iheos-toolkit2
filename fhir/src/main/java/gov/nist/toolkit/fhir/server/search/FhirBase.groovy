package gov.nist.toolkit.fhir.server.search

import gov.nist.toolkit.fhir.server.utility.FhirClient
import gov.nist.toolkit.fhir.shared.searchModels.LogicalIdSM
import gov.nist.toolkit.fhir.shared.searchModels.ResourceType
import org.hl7.fhir.instance.model.api.IBaseResource

class FhirBase implements IFhirBase {
    String baseUrl

    FhirBase(String baseUrl) {
        this.baseUrl = baseUrl
        assert baseUrl
    }

    IBaseResource read(ResourceType resourceType, LogicalIdSM logicalId) {
        assert resourceType
        assert logicalId
        String url = "${baseUrl}/${resourceType}/${logicalId}"
        FhirClient.readResource(url)
    }

    /**
     *
     * @param resourceType
     * @param queryString excludes ? prefix
     * @param parsms list of name=value
     * @return
     */
    List<IBaseResource> search(ResourceType resourceType, List<String> params) {
        new FhirClient().search(baseUrl, resourceType.toString(), params).values()
    }
}
