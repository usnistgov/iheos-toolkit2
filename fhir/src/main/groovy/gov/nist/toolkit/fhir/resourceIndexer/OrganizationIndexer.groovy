package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import groovy.transform.CompileStatic
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Identifier
import org.hl7.fhir.dstu3.model.Organization
import org.hl7.fhir.dstu3.model.Reference

/**
 * Supports:
 *    for mCSD:
 *       Common Parameters as specified in 3.Y1.4.1.2.1 and defined at http://hl7.org/fhir/STU3/search.html#all.
 *       _id and _profile
 *       Parameters for the Organization Option as specified in 3.Y1.4.1.2.2 and defined at
 *       http://hl7.org/fhir/STU3/organization.html#search.
 *       active, identifier, name, partof, and type
 */
@CompileStatic
class OrganizationIndexer implements IResourceIndexer {
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

        Organization organization = (Organization) theResource

        addIndex(Organization.SP_ACTIVE, organization.active.toString())

        organization.identifier.each {
            addIndex(Organization.SP_IDENTIFIER, it.value)
        }

        addIndex(Organization.SP_NAME, organization.name)

        Reference po = organization.partOf
        if (po) {
            addIndex(Organization.SP_PARTOF, po.reference)
        }

        organization.type.each {
            addIndex(Organization.SP_TYPE,
                    "${it.coding.system}|${it.coding.code}")
        }

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
