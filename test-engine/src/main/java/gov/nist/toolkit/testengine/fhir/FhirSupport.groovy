package gov.nist.toolkit.testengine.fhir

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
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
        return null
    }

}
