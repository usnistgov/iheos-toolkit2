package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

class StatusValidater extends AbstractValidater {
    int code

    @Override
    ValidaterResult validate(FhirSimulatorTransaction transaction) {
            boolean match = transaction.responseHeaders.statusLine.statusCode == code
            new ValidaterResult(transaction, this, match)
    }

    StatusValidater(SimReference theSimReference, String statusCode) {
        super(theSimReference, "Status code ${statusCode} was returned")
        code = statusCode.toInteger()
    }
}
