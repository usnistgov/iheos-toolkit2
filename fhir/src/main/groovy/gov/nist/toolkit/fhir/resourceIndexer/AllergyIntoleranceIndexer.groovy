package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.AllergyIntolerance

/**
 *
 */
class AllergyIntoleranceIndexer implements IResourceIndexer {
    /**
     * Index the AllergyIntollerance index attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        ResourceIndex resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        AllergyIntolerance allergyIntolerance = (AllergyIntolerance) theResource

        resourceIndex.add(new ResourceIndexItem(AllergyIntolerance.SP_PATIENT, allergyIntolerance.getPatient().getReference()))

        return resourceIndex
    }

}
