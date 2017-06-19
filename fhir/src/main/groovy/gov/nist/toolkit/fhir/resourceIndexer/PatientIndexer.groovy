package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Patient

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

        // Add specialization here

        Patient patient = (Patient) theResource

        String field = Patient.SP_FAMILY
        String value = patient.getName().get(0).getFamily()

        resourceIndex.add(new ResourceIndexItem(field, value))



        return resourceIndex
    }
}
