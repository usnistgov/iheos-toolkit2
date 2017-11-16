package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.testengine.engine.StepContext
import org.apache.axiom.om.OMElement

class ProvideDocumentBundleTransaction extends FhirCreateTransaction {
    String withPatientId = null

    ProvideDocumentBundleTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    @Override
    protected String getBasicTransactionName() {
        return 'pdb'
    }

}
