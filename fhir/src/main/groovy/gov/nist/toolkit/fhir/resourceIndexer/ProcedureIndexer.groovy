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

        Procedure procedure = (Procedure) theResource

        Reference subject = procedure.subject
        if (subject) addIndex(Procedure.SP_SUBJECT, subject.reference)

        return resourceIndex
    }

    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}