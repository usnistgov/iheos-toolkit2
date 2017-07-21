package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Encounter

/**
 *
 */
class EncounterIndexer implements IResourceIndexer {
    private ResourceIndex resourceIndex = null
    /**
     * Index the Encounter resource attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
         resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        Encounter rs = (Encounter) theResource

        if (rs.appointment)
            addIndex(Encounter.SP_APPOINTMENT, rs.appointment.reference)
        if (rs.class_)
            addIndex(Encounter.SP_CLASS, "${rs.class_.system}|${rs.class_.getCode()}")
        rs.diagnosis.each {
            addIndex(Encounter.SP_DIAGNOSIS, it.condition.reference)
        }
        rs.episodeOfCare.each {
            addIndex(Encounter.SP_EPISODEOFCARE, it.reference)
        }
        rs.identifier.each {
            addIndex(Encounter.SP_IDENTIFIER, it.value)
        }
        rs.incomingReferral.each {
            addIndex(Encounter.SP_INCOMINGREFERRAL, it.reference)
        }
        rs.location.each {
            addIndex(Encounter.SP_LOCATION, it.location.reference)
        }
        if (rs.partOf)
            addIndex(Encounter.SP_PART_OF, rs.partOf.reference)

        rs.reason.each {
            addIndex(Encounter.SP_REASON, "${it.coding.system}|${it.coding.code}")
        }

        if(rs.serviceProvider)
            addIndex(Encounter.SP_SERVICE_PROVIDER, rs.serviceProvider.reference)

        if (rs.hospitalization)
            rs.hospitalization.specialArrangement.each {
                addIndex(Encounter.SP_SPECIAL_ARRANGEMENT, "${it.coding.system}|${it.coding.code}")
            }

        addIndex(Encounter.SP_STATUS, "${rs.status.system}|${rs.status.toCode()}")

        rs.type.each {
            addIndex(Encounter.SP_TYPE, "${it.coding.system}|${it.coding.code}")
        }

        resourceIndex.add(new ResourceIndexItem(Encounter.SP_PATIENT, rs.getSubject().getReference()))

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }

}
