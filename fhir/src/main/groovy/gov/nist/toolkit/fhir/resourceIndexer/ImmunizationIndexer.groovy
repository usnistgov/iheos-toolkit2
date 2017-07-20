package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.Immunization
import org.hl7.fhir.dstu3.model.DomainResource

/**
 *
 */
class ImmunizationIndexer implements IResourceIndexer {
    /**
     * Index the Immunization resource attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        ResourceIndex resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        Immunization immunization = (Immunization) theResource

        resourceIndex.add(new ResourceIndexItem(Immunization.SP_PATIENT, immunization.getPatient().getReference()))

        return resourceIndex
    }

}
