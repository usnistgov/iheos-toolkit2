package gov.nist.toolkit.fhir.server.search

import gov.nist.toolkit.fhir.shared.searchModels.LogicalIdSM
import gov.nist.toolkit.fhir.shared.searchModels.ResourceType

abstract class BasicSearch {
    ResourceType resourceType = null
    LogicalIdSM logicalId = null
    List<FhirBase> supportingFhirBases = []
    FhirBase fhirBase = null // target of the query

    abstract String asQueryString()

    BasicSearch(FhirBase fhirBase, ResourceType resourceType, LogicalIdSM logicalId) {
        this.fhirBase = fhirBase
        this.resourceType = resourceType
        this.logicalId = logicalId
    }

    boolean isValid() {
        return resourceType != null && logicalId != null;
    }

    boolean isRead() {
        return logicalId != null;
    }

    boolean isSearch() {
        return logicalId == null;
    }

    String asReadString() {
        if (valid && read) {
            return "/" + resourceType.toString() + "/" + logicalId.getValue();
        }
        return null;
    }
}
