package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.HealthcareService

/**
 * Supports:
 *    for mCSD:
 *       Common Parameters as specified in 3.Y1.4.1.2.1 and defined at http://hl7.org/fhir/STU3/search.html#all.
 *       _id and _profile
 *       Parameters for the Organization Option as specified in 3.Y1.4.1.2.5 and defined at
 *       http://hl7.org/fhir/STU3/healthcareservice.html#search.
 *       active, identifier, location, name, organization, and type
 */
class HealthcareServiceIndexer implements IResourceIndexer {
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

        HealthcareService healthcareService = (HealthcareService) theResource

        addIndex(HealthcareService.SP_ACTIVE, healthcareService.active.toString())

        healthcareService.identifier.each {
            addIndex(HealthcareService.SP_IDENTIFIER, it.value)
        }
        healthcareService.location.each {
            addIndex(HealthcareService.SP_LOCATION, it.reference)
        }

        addIndex(HealthcareService.SP_NAME, healthcareService.name)

        healthcareService.providedBy.each {
            addIndex(HealthcareService.SP_ORGANIZATION, it.reference)
        }
        healthcareService.type.each {
            addIndex(HealthcareService.SP_TYPE,
                    "${it.coding.system}|${it.coding.code}")
        }

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
