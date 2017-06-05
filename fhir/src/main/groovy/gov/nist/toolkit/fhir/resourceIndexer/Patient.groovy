package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.SimResource
/**
 *
 */
class Patient implements IResourceIndexer {
    /**
     * Index the Patient resource attributes
     * @param json - JsonSlurper representation of resource
     * @param simResource - details about where the resource will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(Object json, SimResource simResource) {
        ResourceIndex resourceIndex = new Base().build(json, simResource)

        // Add specialization here


        return resourceIndex
    }
}
