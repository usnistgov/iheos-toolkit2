package gov.nist.toolkit.fhir.resourceIndexer;

import gov.nist.toolkit.fhir.support.ResourceIndex;
import gov.nist.toolkit.fhir.support.SimResource;

/**
 *
 */
public interface IResourceIndexer {
    ResourceIndex build(Object json, SimResource simResource);
}
