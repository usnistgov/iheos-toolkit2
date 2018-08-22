package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.transactions.BasicTransaction

// instance variables are initialized through reflection
// their names must match XML and be type String
class StatusValidater extends AbstractFhirValidater {
    String statusCode

    void setStatusCode(String statusCode) {
        this.statusCode = statusCode
        filterDescription = "Response returned status ${statusCode}"
    }

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transactionInstance) {
            boolean match = transactionInstance.responseHeaders.statusLine.statusCode == (statusCode as Integer)
            new ValidaterResult(transactionInstance, this, match)
    }

}
