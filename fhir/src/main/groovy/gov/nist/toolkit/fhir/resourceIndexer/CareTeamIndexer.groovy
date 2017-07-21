package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Reference
import org.hl7.fhir.dstu3.model.CareTeam

class CareTeamIndexer implements IResourceIndexer {
    private ResourceIndex resourceIndex = null
    /**
     * Index the Location index attributes
     * @param json - JsonSlurper representation of index
     * @param simResource - details about where the index will be stored
     * @return newly built index
     */
    @Override
    ResourceIndex build(DomainResource theResource, String id) {
        resourceIndex = new Base().build(theResource, id)

        // Add specialization here

        CareTeam team = (CareTeam) theResource

        team.category.each {
            addIndex(CareTeam.SP_CATEGORY, "${it.coding.system}|${it.coding.code}")
        }
        if (team.context) addIndex(CareTeam.SP_CONTEXT, team.context.reference)

        team.identifier.each {
            addIndex(CareTeam.SP_IDENTIFIER, it.value)
        }

        team.participant.each {
            if (it.member)
                addIndex(CareTeam.SP_PARTICIPANT, it.member.reference)
        }

        Reference subject = team.subject
        if (subject) {
            addIndex(CareTeam.SP_SUBJECT, subject.reference)
        }

        if (team.status) {
            addIndex(CareTeam.SP_STATUS, "${team.status.system}|${team.status.toCode()}")
        }

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
