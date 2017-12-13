package gov.nist.toolkit.fhir.simulators.mhd

import gov.nist.toolkit.fhir.server.utility.WrapResourceInHttpResponse
import gov.nist.toolkit.fhir.simulators.mhd.errors.AbstractError
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.HttpResponse
import org.hl7.fhir.dstu3.model.OperationOutcome

class ErrorLoggerAsHttpResponse {

//    ErrorLoggerAsHttpResponse(ErrorLogger errorLogger, SimProxyBase base) {
//        super(errorLogger)
//        OperationOutcome oo = new OperationOutcome()
//        errorLogger.errors.each { error ->
//            OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
//            com.setSeverity(OperationOutcome.IssueSeverity.ERROR)
//            com.setDiagnostics(error)
//            oo.addIssue(com)
//        }
//        WrapResourceInHttpResponse.wrap(base, oo, HttpStatus.SC_BAD_REQUEST)
//    }

    static HttpResponse buildHttpResponse(SimProxyBase base, ErrorLogger errorLogger) {
        OperationOutcome oo = new OperationOutcome()
        errorLogger.errors.each { AbstractError error ->
            OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
            com.setSeverity(OperationOutcome.IssueSeverity.ERROR)
            com.setDiagnostics(error.toString())
            oo.addIssue(com)
        }
        WrapResourceInHttpResponse.wrap(base.chooseContentType(), oo, HttpStatus.SC_BAD_REQUEST)
    }
}
