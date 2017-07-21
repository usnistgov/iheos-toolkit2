package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.AllergyIntolerance

/**
 *
 */
class AllergyIntoleranceIndexer implements IResourceIndexer {
    private ResourceIndex resourceIndex = null
    /**
     * Index the AllergyIntollerance index attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        AllergyIntolerance rs = (AllergyIntolerance) theResource

        if (rs.asserter)
            addIndex(AllergyIntolerance.SP_ASSERTER, rs.asserter.reference)


        rs.category.each {
            addIndex(AllergyIntolerance.SP_CATEGORY, "${it.toSystem()}|${it.toString()}")
        }
        if (rs.clinicalStatus)
            addIndex(AllergyIntolerance.SP_CLINICAL_STATUS,
                    "${rs.clinicalStatus.getSystem()}|${rs.clinicalStatus.getDisplay()}")

        if (rs.code)
            addIndex(AllergyIntolerance.SP_CODE, "${rs.code.coding.system}|${rs.code.coding.code}")

        if (rs.criticality)
            addIndex(AllergyIntolerance.SP_CRITICALITY, "${rs.criticality.getSystem()}|${rs.criticality.toString()}")

        rs.identifier.each {
            addIndex(AllergyIntolerance.SP_IDENTIFIER, it.value)
        }

        rs.reaction.each {
            it.manifestation.each {
                addIndex(AllergyIntolerance.SP_MANIFESTATION, "${it.coding.system}|${it.coding.code}")
            }
            if (it.exposureRoute)
                addIndex(AllergyIntolerance.SP_ROUTE, "${it.exposureRoute.coding.system}|${it.exposureRoute.coding.code}")
            if (it.severity)
                addIndex(AllergyIntolerance.SP_SEVERITY, "${it.severity.system}|${it.severity.toCode()}")
        }

        if (rs.type)
            addIndex(AllergyIntolerance.SP_TYPE, "${rs.type.system}|${rs.type.toCode()}")

        addIndex(AllergyIntolerance.SP_VERIFICATION_STATUS, "${rs.verificationStatus.system}|${rs.verificationStatus.toCode()}")

        if(rs.recorder)
            addIndex(AllergyIntolerance.SP_RECORDER, rs.recorder.reference)

        resourceIndex.add(new ResourceIndexItem(AllergyIntolerance.SP_PATIENT, rs.getPatient().getReference()))

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }

}
