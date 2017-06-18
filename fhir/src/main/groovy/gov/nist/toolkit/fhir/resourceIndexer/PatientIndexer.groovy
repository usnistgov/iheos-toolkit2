package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.SimResource
/**
 *
 */
class PatientIndexer implements IResourceIndexer {
    /**
     * Index the Patient index attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(Object json, SimResource simResource) {
        ResourceIndex resourceIndex = new Base().build(json, simResource)

//        String field = Patient.SP_FAMILY
//        String value
//
//        resourceIndex.add(new ResourceIndexItem(field, value))

        // Add specialization here


        return resourceIndex
    }
}
