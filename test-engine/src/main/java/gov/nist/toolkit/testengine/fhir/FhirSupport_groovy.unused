package gov.nist.toolkit.testengine.fhir

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.server.utility.WrapResourceInHttpResponse
import gov.nist.toolkit.utilities.io.Io
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.HttpResponse
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.DomainResource
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.dstu3.model.codesystems.IssueSeverity
import org.hl7.fhir.instance.model.api.IBaseReference
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

    static String format(IBaseResource resource)  {
        FhirContext ctx = ToolkitFhirContext.get()
        ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource)
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

    static DomainResource duplicate(DomainResource resource) {
        FhirContext ctx = ToolkitFhirContext.get()
        ctx.newJsonParser().parseResource(ctx.newJsonParser().encodeResourceToString(resource)) as DomainResource
    }

    static DomainResource withoutContained(DomainResource resource) {
        DomainResource copy = duplicate(resource)
        copy.setContained(new ArrayList())
        copy
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

    static List<String> operationOutcomeIssues(IBaseResource oo) {
        assert oo instanceof OperationOutcome
        def issues = []
        oo.issue.each { OperationOutcome.OperationOutcomeIssueComponent issue ->
            issue.with {
                issues << "Error ${code}: ${diagnostics}"
            }
        }

        issues
    }

    static boolean isError(OperationOutcome.OperationOutcomeIssueComponent issue) {
        issue.severity == IssueSeverity.FATAL || issue.severity == IssueSeverity.ERROR
    }

    static boolean isWarningOrInfo(OperationOutcome.OperationOutcomeIssueComponent issue) {
        issue.severity == IssueSeverity.WARNING || issue.severity == IssueSeverity.INFORMATION
    }

    static List<String> operationOutcomeErrors(IBaseResource oo) {
        assert oo instanceof OperationOutcome
        OperationOutcome theoo = oo
        def issues = []
        oo.issue.each { OperationOutcome.OperationOutcomeIssueComponent issue ->
            if (isError(issue)) {
                issue.with {
                    issues << "Error ${code}: ${diagnostics}"
                }
            }
        }

        issues
    }

    static List<String> operationOutcomeWarningsOrInfo(IBaseResource oo) {
        assert oo instanceof OperationOutcome
        OperationOutcome theoo = oo
        def issues = []
        oo.issue.each { OperationOutcome.OperationOutcomeIssueComponent issue ->
            if (isWarningOrInfo(issue)) {
                issue.with {
                    issues << "Warning ${code}: ${diagnostics}"
                }
            }
        }

        issues
    }

    static OperationOutcome operationOutcomeFromThrowable(Throwable e) {
        OperationOutcome oo = new OperationOutcome()
        OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
        com.setSeverity(OperationOutcome.IssueSeverity.FATAL)
        com.setCode(OperationOutcome.IssueType.EXCEPTION)
        com.setDiagnostics(e.message)
        exception_details(e).each { com.addLocation(it)}
        oo.addIssue(com)
        oo
    }

    static List<String> exception_details(Throwable e) {
        def trace = []
        if (e == null)
            return trace
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);

        String stackTrace = new String(baos.toByteArray());
        trace << e.class.simpleName
        Scanner scanner = new Scanner(stackTrace);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.contains("gov.nist.toolkit")) continue;
            trace << line
        }

        return trace
    }


    static HttpResponse operationOutcomeFromThrowableInHttpResponse(Throwable e) {
        WrapResourceInHttpResponse.wrap('application/fhir+json', operationOutcomeFromThrowable(e), HttpStatus.SC_OK)
    }

}
