package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

class ValidaterResult {
    FhirSimulatorTransaction transaction
    AbstractValidater filter
    boolean match

    ValidaterResult(FhirSimulatorTransaction transaction, AbstractValidater filter, boolean match) {
        this.transaction = transaction
        this.filter = filter
        this.match = match
    }
}
