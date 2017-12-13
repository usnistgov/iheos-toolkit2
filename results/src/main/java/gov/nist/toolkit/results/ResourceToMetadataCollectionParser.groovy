package gov.nist.toolkit.results

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.registrymetadata.client.DocumentEntry
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.registrymetadata.client.SubmissionSet
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.DocumentManifest
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.instance.model.api.IBaseResource

class ResourceToMetadataCollectionParser {
    MetadataCollection col = null


    def add(IBaseResource res) {
        if (res instanceof DocumentReference) {
            initCollection()
            DocumentEntry de = new DocumentEntry()
            parse(de, res)
            col.docEntries.add(de)
        } else if (res instanceof DocumentManifest) {
            initCollection()
            SubmissionSet ss = new SubmissionSet()
            parse(ss, res)
            col.submissionSets.add(ss)
        } else if (res instanceof Bundle) {
            res.entry.each { Bundle.BundleEntryComponent comp ->
                add(comp.getResource())
            }
        } else {
            // not a resource we have a GWT model for - add the JSON
            String json = ToolkitFhirContext.get().newJsonParser().encodeResourceToString(res)
            col.others.add(json)
        }
    }

    def initCollection() {
        if (!col)
            col = new MetadataCollection()
    }

    /**
     * translate the DocumentReference to the DocumentEntry
     * @param de
     * @param dr
     */
    def parse(DocumentEntry de, DocumentReference dr) {
        // use MhdGenerator.buildSubmission at line 582
        // where it calls addExtrinsicObject
    }

    /**
     * translate the DocmentReference to the SubmissionSet
     * @param ss
     * @param dm
     */
    def parse(SubmissionSet ss, DocumentManifest dm) {
        // use MhdGenerator.buildSubmission at line 560
        // where it calls addSubmissionSet
    }

    /**
     * fill in details from Binary
     * @param de
     * @param b
     */
    def parse(DocumentEntry de, Binary b) {
        // some details at MhdGenerator.buildSubmission at line 572
    }

    public MetadataCollection get() { return col; }

}
