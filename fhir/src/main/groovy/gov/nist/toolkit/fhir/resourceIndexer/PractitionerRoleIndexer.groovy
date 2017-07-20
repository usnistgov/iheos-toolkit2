package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.PractitionerRole

/**
 * Supports:
 *    for mCSD:
 *       Common Parameters as specified in 3.Y1.4.1.2.1 and defined at http://hl7.org/fhir/STU3/search.html#all.
 *       _id and _profile
 *       Parameters for the Practitioner Option as specified in 3.Y1.4.1.2.4 and defined at
 *       http://hl7.org/fhir/STU3/practitionerrole.html#search.
 *       active, location, organization, practitioner, role, service, specialty
 */
class PractitionerRoleIndexer implements IResourceIndexer {
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

        PractitionerRole practitionerRole = (PractitionerRole) theResource

        addIndex(PractitionerRole.SP_ACTIVE, practitionerRole.active.toString())

        practitionerRole.location.each {
            addIndex(PractitionerRole.SP_LOCATION, it.reference)
        }
        practitionerRole.organization.each {
            addIndex(PractitionerRole.SP_ORGANIZATION, it.reference)
        }
        practitionerRole.practitioner.each {
            addIndex(PractitionerRole.SP_PRACTITIONER, it.reference)
        }
        practitionerRole.code.each {
            addIndex(PractitionerRole.SP_ROLE,
                    "${it.coding.system}|${it.coding.code}")
        }
        practitionerRole.healthcareService.each {
            addIndex(PractitionerRole.SP_SERVICE, it.reference)
        }
        practitionerRole.specialty.each {
            addIndex(PractitionerRole.SP_SPECIALTY,
                    "${it.coding.system}|${it.coding.code}")
        }

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
