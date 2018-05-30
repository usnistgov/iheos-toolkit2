package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

class StatusFhirValidater extends AbstractFhirValidater {
    int code

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
            boolean match = transaction.responseHeaders.statusLine.statusCode == code
            new ValidaterResult(transaction, this, match)
    }

    StatusFhirValidater(SimReference theSimReference, String statusCode) {
        super(theSimReference, "Status code ${statusCode} was returned")
        code = statusCode.toInteger()
    }
}
