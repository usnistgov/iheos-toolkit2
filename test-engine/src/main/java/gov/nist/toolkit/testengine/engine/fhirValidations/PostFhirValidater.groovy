package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

class PostFhirValidater extends AbstractFhirValidater {

    PostFhirValidater(SimReference theSimReference) {
        super(theSimReference, 'Request is HTTP POST')

    }

    ValidaterResult validate(FhirSimulatorTransaction transaction) {
            boolean match = transaction.requestHeaders.requestLine.method == 'POST'
            new ValidaterResult(transaction, this, match)
    }

}
