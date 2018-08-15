package war.toolkitx.testkit.plugins.XdsAssertion

import gov.nist.toolkit.testengine.engine.SimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.xds.AbstractXdsValidater

class SingleDocSubmissionValidater extends AbstractXdsValidater {

    SingleDocSubmissionValidater() {
        filterDescription = 'Submission of a Single Document Entry'
    }

    @Override
    ValidaterResult validate(SimulatorTransaction transaction) {
        // FIXME
        boolean match = transaction.request instanceof String && isSingleDocSubmission(transaction.request) && !isErrors()
        new ValidaterResult(transaction, this, match)
    }

    private boolean isSingleDocSubmission(String request) {
        // skb TODO. Do xpath checking here.
        // FIXME
        return true
    }
}
