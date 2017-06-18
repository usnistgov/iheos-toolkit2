package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import org.hl7.fhir.dstu3.model.DomainResource

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
    ResourceIndex build(DomainResource theResource, String id) {
        ResourceIndex resourceIndex = new Base().build(theResource, id)

//        String field = Patient.SP_FAMILY
//        String value
//
//        resourceIndex.add(new ResourceIndexItem(field, value))

        // Add specialization here


        return resourceIndex
    }
}
