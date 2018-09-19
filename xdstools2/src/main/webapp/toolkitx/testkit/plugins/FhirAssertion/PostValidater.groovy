package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.transactions.BasicTransaction

class PostValidater extends AbstractFhirValidater {

    PostValidater() {
        filterDescription = 'Transaction carried in HTTP POST'
    }

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transactionInstance) {
        boolean match = transactionInstance.requestHeaders.requestLine.method == 'POST'
        new ValidaterResult(transactionInstance, this, match)
    }

}
