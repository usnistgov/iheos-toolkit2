package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Reference
import org.hl7.fhir.dstu3.model.Procedure

class ProcedureIndexer implements IResourceIndexer {
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

        Procedure rs = (Procedure) theResource

        rs.basedOn.each {
            addIndex(Procedure.SP_BASED_ON, it.reference)
        }

        if (rs.category)
            addIndex(Procedure.SP_CATEGORY, "${rs.category.coding.system}|${rs.category.coding.code}")

        if (rs.code)
            addIndex(Procedure.SP_CODE, "${rs.code.coding.system}|${rs.code.coding.code}")

        if (rs.context)
            addIndex(Procedure.SP_CONTEXT, rs.context.reference)

        rs.definition.each {
            addIndex(Procedure.SP_DEFINITION, it.reference)
        }

        rs.identifier.each {
            addIndex(Procedure.SP_IDENTIFIER, it.value)
        }

        if (rs.location)
            addIndex(Procedure.SP_LOCATION, rs.location.reference)

        rs.partOf.each {
            addIndex(Procedure.SP_PART_OF, it.reference)
        }

        rs.performer.each {
            addIndex(Procedure.SP_PERFORMER, it.actor.reference)
        }

        addIndex(Procedure.SP_STATUS, "${rs.status.system}|${rs.status.toCode()}")

        Reference subject = rs.subject
        if (subject) addIndex(Procedure.SP_SUBJECT, subject.reference)

        return resourceIndex
    }

    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
