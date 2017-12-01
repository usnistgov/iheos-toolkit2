package gov.nist.toolkit.testengine.fhir

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.utilities.io.Io
import org.hl7.fhir.dstu3.model.Binary
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


}
