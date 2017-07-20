package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.UriType

/**
 *
 */
class Base implements IResourceIndexer {

    /**
     * Index the base attributes: id, type, and profiles (0..*)
     * @param json - JsonSlurper representation of resource
     * @param simResource - details about where the resource will be stored
     * @return newly built index
     */
    ResourceIndex build(DomainResource theResource, String id) {
        ResourceIndex ri = new ResourceIndex()

        String resourceType = theResource.getClass().simpleName
        ri.add(new ResourceIndexItem('type', resourceType))

        ri.add(new ResourceIndexItem('id', id))

        for (UriType profile : theResource.meta.profile) {
            ri.add(new ResourceIndexItem('_profile', profile.toString()))
        }

        return ri
    }
}
