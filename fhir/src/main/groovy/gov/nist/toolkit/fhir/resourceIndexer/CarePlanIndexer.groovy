package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.CarePlan
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Reference

class CarePlanIndexer implements IResourceIndexer {
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

        CarePlan plan = (CarePlan) theResource


        plan.activity.each {
            if (it.detail && it.detail.code) {
                addIndex(CarePlan.SP_ACTIVITY_CODE,
                        "${it.detail.code.coding.system}|${it.detail.code.coding.code}" )
            }
            if (it.reference) {
                addIndex(CarePlan.SP_ACTIVITY_REFERENCE, it.reference.reference)
            }
            if (it.detail) {
                it.detail.performer.each {
                    addIndex(CarePlan.SP_PERFORMER, it.reference)
                }
            }
        }

        plan.basedOn.each {
            addIndex(CarePlan.SP_BASED_ON, it.reference)
        }

        plan.careTeam.each {
            addIndex(CarePlan.SP_CARE_TEAM, it.reference)
        }

        plan.category.each {
            addIndex(CarePlan.SP_CATEGORY, "${it.coding.system}|${it.coding.code}")
        }

        plan.addresses.each {
            addIndex(CarePlan.SP_CONDITION, it.reference)
        }
        plan.context.each {
            addIndex(CarePlan.SP_CONTEXT, it.reference)
        }
        plan.definition.each {
            addIndex(CarePlan.SP_DEFINITION, it.reference)
        }
        plan.goal.each {
            addIndex(CarePlan.SP_GOAL, it.reference)
        }
        plan.identifier.each {
            addIndex(CarePlan.SP_IDENTIFIER, it.value)
        }
        addIndex(CarePlan.SP_INTENT, "${plan.intent.system}|${plan.intent.toCode()}")

        plan.partOf.each {
            addIndex(CarePlan.SP_PART_OF, it.reference)
        }

        addIndex(CarePlan.SP_SUBJECT, plan.subject.reference)

        Reference subject = plan.subject
        if (subject) {
            addIndex(CarePlan.SP_SUBJECT, subject.reference)
        }

        plan.replaces.each {
            addIndex(CarePlan.SP_REPLACES, it.reference)
        }

        addIndex(CarePlan.SP_STATUS, "${plan.status.system}|${plan.status.toCode()}")

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
