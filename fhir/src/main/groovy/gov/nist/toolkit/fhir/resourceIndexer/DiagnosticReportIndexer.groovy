package gov.nist.toolkit.fhir.resourceIndexer

import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.ResourceIndexItem
import org.hl7.fhir.dstu3.model.CodeableConcept
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.DiagnosticReport
import org.hl7.fhir.dstu3.model.Reference

class DiagnosticReportIndexer implements IResourceIndexer {
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

        DiagnosticReport diagnosticReport = (DiagnosticReport) theResource

        Reference subject = diagnosticReport.subject
        if (subject) addIndex(DiagnosticReport.SP_SUBJECT, subject.reference)

        CodeableConcept category = diagnosticReport.category
        if (category) addIndex(DiagnosticReport.SP_CATEGORY,
                "${category.coding.system}|${category.coding.code}")

        CodeableConcept code = diagnosticReport.code
        if (category) addIndex(DiagnosticReport.SP_CODE,
                "${code.coding.system}|${code.coding.code}")


        return resourceIndex
    }

    private void addIndex(String name, String value) {
        if (name && value)
            resourceIndex.add(new ResourceIndexItem(name, value))
    }
}
