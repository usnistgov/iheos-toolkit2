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

        resourceIndex.add(new ResourceIndexItem(Patient.SP_FAMILY, patient.getName().get(0).getFamily()))

        patient.name.get(0).given.each {
            resourceIndex.add(new ResourceIndexItem(Patient.SP_GIVEN, it.value))
        }


        return resourceIndex
    }
}
