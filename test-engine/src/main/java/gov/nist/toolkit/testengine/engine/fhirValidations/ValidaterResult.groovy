package gov.nist.toolkit.testengine.engine.fhirValidations

import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction

class ValidaterResult {
    FhirSimulatorTransaction transaction
    AbstractFhirValidater filter
    boolean match

    ValidaterResult(FhirSimulatorTransaction transaction, AbstractFhirValidater filter, boolean match) {
        this.transaction = transaction
        this.filter = filter
        this.match = match
    }
}
