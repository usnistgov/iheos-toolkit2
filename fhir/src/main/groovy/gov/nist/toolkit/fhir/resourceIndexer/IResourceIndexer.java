package gov.nist.toolkit.fhir.resourceIndexer;

import gov.nist.toolkit.fhir.support.ResourceIndex;
import org.hl7.fhir.dstu3.model.DomainResource;

/**
 *
 */
public interface IResourceIndexer {
    ResourceIndex build(DomainResource theResource, String id);
}
