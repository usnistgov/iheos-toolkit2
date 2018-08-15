package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.DocumentManifest
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.Resource

class SingleDocSubmissionValidater extends AbstractFhirValidater {

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
        boolean match = transaction.request instanceof Bundle && isSingleDocSubmission(transaction.request) && !isErrors()
        new ValidaterResult(transaction, this, match)
    }

    private boolean isSingleDocSubmission(Bundle bundle) {
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
}
