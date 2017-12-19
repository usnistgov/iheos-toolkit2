package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.DocumentManifest
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.Resource

class SingleDocSubmissionValidater extends AbstractValidater {
    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
            boolean match = transaction.request instanceof Bundle && isSingleDocSubmission(transaction.request) && !isErrors()
            new ValidaterResult(transaction, this, match)
    }

    boolean isSingleDocSubmission(Bundle bundle) {
        DocumentManifest documentManifest = null
        List<DocumentReference> documentReferences = []

        bundle.entry.each { Bundle.BundleEntryComponent component ->
            Resource resource = component.getResource()
            if (resource instanceof DocumentManifest) {
                if (documentManifest)
                    error("Extra DocumentManifest found in submission")
                documentManifest = resource
            }
            if (resource instanceof DocumentReference) {
                documentReferences.add(resource)
            }
        }
        documentManifest && documentReferences.size() > 0
    }

    SingleDocSubmissionValidater(SimReference theSimReference) {
        super(theSimReference, 'Submission of a Single Document Reference with the Document Manifest')
    }
}
