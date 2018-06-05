package war.toolkitx.testkit.plugins.FhirAssertion

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.fhirValidations.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.fhirValidations.ValidaterResult

// instance variables are initialized through reflection
// their names must match XML and be type String
class StatusValidater extends AbstractFhirValidater {
    String statusCode

    void setStatusCode(String statusCode) {
        this.statusCode = statusCode
        filterDescription = "Response returned status ${statusCode}"
    }

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
            boolean match = transaction.responseHeaders.statusLine.statusCode == (statusCode as Integer)
            new ValidaterResult(transaction, this, match)
    }

}
