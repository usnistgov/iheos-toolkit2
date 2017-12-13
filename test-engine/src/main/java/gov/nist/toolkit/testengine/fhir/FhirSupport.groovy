package gov.nist.toolkit.testengine.fhir

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.server.utility.WrapResourceInHttpResponse
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.ExceptionUtil
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.HttpResponse
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.instance.model.api.IBaseResource

class FhirSupport {

    static IBaseResource parse(String content) {
        FhirContext ctx = ToolkitFhirContext.get()
        content = content.trim()
        if (content.startsWith('{')) {
            return ctx.newJsonParser().parseResource(content)
        } else {
            return ctx.newXmlParser().parseResource(content)
        }
    }


    static String format(String content) {
        FhirContext ctx = ToolkitFhirContext.get()
        content = content.trim()
        IBaseResource resource
        if (content.startsWith('{')) {
            resource = ctx.newJsonParser().parseResource(content)
            ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource)
        } else {
            resource =  ctx.newXmlParser().parseResource(content)
            ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resource)
        }
    }

    static mimeTypes = [
            txt: 'text/plain',
            pdf: 'application/pdf',
            xml: 'text/xml'
    ]

    static mimeType(File file) {
        String name = file.name
        def parts = name.split('\\.')
        def ext = parts[parts.size()-1]
        mimeTypes[ext]
    }

    static Binary binaryFromFile(File file) {
        Binary binary = new Binary()
        binary.content = Io.bytesFromFile(file)
        binary.contentType = mimeType(file)
        return binary
    }

    static List<String> operationOutcomeIssues(OperationOutcome oo) {
        def issues = []
        oo.issue.each { OperationOutcome.OperationOutcomeIssueComponent issue ->
            issue.with {
                issues << "Error ${code}: ${diagnostics}"
            }
        }

        issues
    }

    static OperationOutcome operationOutcomeFromThrowable(Throwable e) {
        OperationOutcome oo = new OperationOutcome()
        OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
        com.setSeverity(OperationOutcome.IssueSeverity.FATAL)
        com.setCode(OperationOutcome.IssueType.EXCEPTION)
        com.setDiagnostics(ExceptionUtil.exception_details(e))
        oo.addIssue(com)
        oo
    }

    static HttpResponse operationOutcomeFromThrowableInHttpResponse(Throwable e) {
        WrapResourceInHttpResponse.wrap('application/fhir+json', operationOutcomeFromThrowable(e), HttpStatus.SC_OK)
    }

}
