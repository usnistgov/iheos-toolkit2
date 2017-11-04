package gov.nist.toolkit.fhir.simulators.proxy.util

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.resourceMgr.ResourceCache
import org.hl7.fhir.instance.model.api.IBaseResource

/**
 *
 */
class ResourceParser {

    static IBaseResource parse(String content) {
        FhirContext ctx = ResourceCache.ctx
        content = content.trim()
        if (content.startsWith('{')) {
            return ctx.newJsonParser().parseResource(content)
        } else {
            return ctx.newXmlParser().parseResource(content)
        }
        return null
    }
}
