package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.Immunization
import org.hl7.fhir.dstu3.model.DomainResource

/**
 *
 */
class ImmunizationIndexer implements IResourceIndexer {
    private ResourceIndex resourceIndex = null
    /**
     * Index the Immunization resource attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        Immunization rs = (Immunization) theResource

        rs.identifier.each {
            addIndex(Immunization.SP_IDENTIFIER, it.value)
        }

        if (rs.location)
            addIndex(Immunization.SP_LOCATION, rs.location.reference)

        if (rs.lotNumber)
            addIndex(Immunization.SP_LOT_NUMBER, rs.lotNumber)

        if (rs.manufacturer)
            addIndex(Immunization.SP_MANUFACTURER, rs.manufacturer.reference)

        addIndex(Immunization.SP_NOTGIVEN, rs.notGiven.toString())

        rs.practitioner.each {
            addIndex(Immunization.SP_PRACTITIONER, it.actor.reference)
        }
        rs.reaction.each {
            addIndex(Immunization.SP_REACTION, it.detail.reference)
        }

        if (rs.explanation) {
            rs.explanation.reason.each {
                addIndex(Immunization.SP_REASON, "${it.coding.system}|${it.coding.code}")
            }
            rs.explanation.reasonNotGiven.each {
                addIndex(Immunization.SP_REASON_NOT_GIVEN, "${it.coding.system}|${it.coding.code}")
            }
        }

        addIndex(Immunization.SP_STATUS, "${rs.status.system}|${rs.status.toCode()}")

        if (rs.vaccineCode)
            addIndex(Immunization.SP_VACCINE_CODE, "${rs.vaccineCode.coding.system}|${rs.vaccineCode.coding.code}")

        resourceIndex.add(new ResourceIndexItem(Immunization.SP_PATIENT, rs.getPatient().getReference()))

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }

}
