package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.SimResource
/**
 *
 */
class Patient implements IResourceIndexer {
    @Override
    ResourceIndex build(Object json, SimResource simResource) {
        ResourceIndex resourceIndex = new Base().build(json, simResource)

        // Add specialization here


        return resourceIndex
    }
}
