package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.testengine.engine.StepContext
import org.apache.axiom.om.OMElement
import org.hl7.fhir.instance.model.api.IBaseResource

class ProvideDocumentBundleTransaction extends FhirCreateTransaction {
    String withPatientId = null

    ProvideDocumentBundleTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        super.doRun(resource, urlExtension)
    }

        @Override
    protected String getBasicTransactionName() {
        return 'pdb'
    }

}
