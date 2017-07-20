package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexDateItem
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Condition

/**
 *
 */
class ConditionIndexer implements IResourceIndexer {
    /**
     * Index the Condition resource attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        ResourceIndex resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        Condition condition = (Condition) theResource

        resourceIndex.add(new ResourceIndexItem(Condition.SP_PATIENT, condition.getSubject().getReference()))
        resourceIndex.add(new ResourceIndexItem(Condition.SP_CATEGORY, condition.getCa))
        resourceIndex.add(new ResourceIndexItem(Condition.SP_CLINICAL_STATUS, condition.getCl))

        return resourceIndex
    }

}
