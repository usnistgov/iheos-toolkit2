package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import groovy.transform.CompileStatic
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.HumanName
import org.hl7.fhir.dstu3.model.Practitioner


/**
 * Supports:
 *    for mCSD:
 *       Common Parameters as specified in 3.Y1.4.1.2.1 and defined at http://hl7.org/fhir/STU3/search.html#all.
 *       _id and _profile
 *       Parameters for the Practitioner Option as specified in 3.Y1.4.1.2.4 and defined at
 *       http://hl7.org/fhir/STU3/practitioner.html#search.
 *       active, identifier, name, given, and family
 */
class PractitionerIndexer implements IResourceIndexer {
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

        Practitioner practitioner = (Practitioner) theResource

        addIndex(Practitioner.SP_ACTIVE, practitioner.active.toString())

        practitioner.identifier.each {
            addIndex(Practitioner.SP_IDENTIFIER, it.value)
        }

        practitioner.name.each {
            addIndex(Practitioner.SP_NAME, it.getNameAsSingleString())
            addIndex(Practitioner.SP_GIVEN, it.getGivenAsSingleString())
            addIndex(Practitioner.SP_FAMILY, it.getFamily())
        }

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
