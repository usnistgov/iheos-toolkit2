package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult

class PostValidater extends AbstractFhirValidater {

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
        boolean match = transaction.requestHeaders.requestLine.method == 'POST'
        new ValidaterResult(transaction, this, match)
    }

}
