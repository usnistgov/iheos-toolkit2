package gov.nist.toolkit.fhir.server.search;

import gov.nist.toolkit.fhir.shared.searchModels.LogicalIdSM;
import gov.nist.toolkit.fhir.shared.searchModels.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;

public interface IFhirBase {
    IBaseResource read(ResourceType resourceType, LogicalIdSM logicalId);
}
