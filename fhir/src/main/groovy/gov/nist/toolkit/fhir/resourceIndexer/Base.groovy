package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import gov.nist.toolkit.fhir.support.SimResource
/**
 *
 */
class Base implements IResourceIndexer {

    /**
     * Index the base attributes
     * @param json - JsonSlurper representation of resource
     * @return
     */
    ResourceIndex build(def json, SimResource simResource) {
        ResourceIndex ri = new ResourceIndex()

        String resourceType = json.resourceType
        ri.add(new ResourceIndexItem('type', resourceType))

        String id = json.id
        ri.add(new ResourceIndexItem('id', id))

        return ri
    }
}
