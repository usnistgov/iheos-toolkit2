package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.soap.http.SoapFault
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.dstu3.model.codesystems.IssueSeverity
import org.hl7.fhir.dstu3.model.codesystems.IssueType

/**
 *
 */
class OperationOutcomeGenerator {

    static OperationOutcome translate(SoapFault fault) {
        OperationOutcome oo = new OperationOutcome()

        OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
        com.setSeverity(IssueSeverity.FATAL)
        com.setCode(IssueType.EXCEPTION)
        com.setDiagnostics(fault.faultReason)
        return oo
    }
}
