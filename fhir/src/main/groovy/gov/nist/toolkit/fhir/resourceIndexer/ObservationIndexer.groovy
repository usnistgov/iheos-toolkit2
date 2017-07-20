package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexDateItem
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import groovy.transform.CompileStatic
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Observation
import org.hl7.fhir.dstu3.model.CodeableConcept
import org.hl7.fhir.dstu3.model.Coding
import org.hl7.fhir.dstu3.model.Period

/**
 *
 */
@CompileStatic
class ObservationIndexer implements IResourceIndexer {
    /**
     * Index the Observation index attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        ResourceIndex resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        Observation observation = (Observation) theResource

        // index the reference to the patient (subject)
        resourceIndex.add(new ResourceIndexItem(Observation.SP_PATIENT, observation.getSubject().getReference()))

        List<CodeableConcept> categories = observation.getCategory();
        for (CodeableConcept concept : categories) {
            for (Coding code : concept.getCoding()) {
                resourceIndex.add(new ResourceIndexItem(Observation.SP_CATEGORY, code.getCode()))
            }
        }

        CodeableConcept code = observation.getCode();
        for (Coding coding : code.getCoding()) {
            resourceIndex.add(new ResourceIndexItem(Observation.SP_CODE, coding.getCode()))
        }

        if (observation.hasEffectiveDateTimeType()) {
            resourceIndex.add(new ResourceIndexDateItem(Observation.SP_DATE, observation.getEffectiveDateTimeType().getValue()))
        }
        else if( observation.hasEffectivePeriod()) {
            Period period = observation.getEffectivePeriod();
            if( period.hasStart())
                resourceIndex.add(new ResourceIndexDateItem('start', period.getStart()))
            if( period.hasEnd())
                resourceIndex.add(new ResourceIndexDateItem('end', period.getEnd()))
        }

        return resourceIndex
    }

}
