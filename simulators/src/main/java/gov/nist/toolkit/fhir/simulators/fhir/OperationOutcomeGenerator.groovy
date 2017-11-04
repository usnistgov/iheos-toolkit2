package gov.nist.toolkit.fhir.simulators.fhir

import gov.nist.toolkit.soap.http.SoapFault
import org.hl7.fhir.dstu3.model.OperationOutcome

/**
 *
 */
class OperationOutcomeGenerator {

    static OperationOutcome translate(SoapFault fault) {
        OperationOutcome oo = new OperationOutcome()

        OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
        com.setSeverity(OperationOutcome.IssueSeverity.FATAL)
        com.setCode(OperationOutcome.IssueType.EXCEPTION)
        com.setDiagnostics(fault.faultReason)
        oo.addIssue(com)
        return oo
    }
}
