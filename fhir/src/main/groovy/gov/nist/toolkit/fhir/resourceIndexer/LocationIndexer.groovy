package gov.nist.toolkit.fhir.resourceIndexer

import com.sun.org.apache.bcel.internal.classfile.Code
import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import groovy.transform.CompileStatic
import org.hl7.fhir.dstu3.model.CodeableConcept
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.Location
import org.hl7.fhir.dstu3.model.Reference
/**
 * Supports:
 *    for mCSD:
 *       Common Parameters as specified in 3.Y1.4.1.2.1 and defined at http://hl7.org/fhir/STU3/search.html#all.
 *       _id and _profile
 *       Parameters for the Location Option as specified in 3.Y1.4.1.2.3 and defined at
 *       http://hl7.org/fhir/STU3/location.html#search.
 *       identifier, name, organization, partOf, status, and type.
 */
class LocationIndexer implements IResourceIndexer {
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

        Location location = (Location) theResource

        location.identifier.each { addIndex(Location.SP_IDENTIFIER, it.value) }

        addIndex(Location.SP_NAME, location.name)

        Reference mo = location.managingOrganization
        if (mo && mo) addIndex(Location.SP_ORGANIZATION, mo.reference)

        Reference po = location.partOf
        if (po) addIndex(location.SP_PARTOF, po.reference)

        Location.LocationStatus status = location.status
        if (status) addIndex(Location.SP_STATUS, "${status.system}|${status.toCode()}")

        CodeableConcept type = location.type
        if (type && type.coding)
            addIndex(Location.SP_TYPE, "${type.coding.system}|${type.coding.code}")

        return resourceIndex
    }
    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
