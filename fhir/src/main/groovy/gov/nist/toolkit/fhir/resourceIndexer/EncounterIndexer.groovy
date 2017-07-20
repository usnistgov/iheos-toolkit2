package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Encounter

/**
 *
 */
class EncounterIndexer implements IResourceIndexer {
    /**
     * Index the Encounter resource attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        ResourceIndex resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        Encounter immunization = (Encounter) theResource

        resourceIndex.add(new ResourceIndexItem(Encounter.SP_PATIENT, immunization.getSubject().getReference()))

        return resourceIndex
    }

}
