package gov.nist.toolkit.fhir.simulators.fhir

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.resourceMgr.ResourceCache
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import org.apache.http.ProtocolVersion
import org.apache.http.entity.StringEntity
import org.apache.http.impl.EnglishReasonPhraseCatalog
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import org.apache.http.protocol.HttpDateGenerator
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class WrapResourceInHttpResponse {

    static BasicHttpResponse wrap(SimProxyBase base, IBaseResource resource, int httpCode) {
        wrap(base, resource, httpCode, EnglishReasonPhraseCatalog.INSTANCE.getReason(httpCode, Locale.ENGLISH))
    }

    static List<String> types = [
            'application/fhir+json',
            'application/fhir+xml'
    ]

    static BasicHttpResponse wrap(SimProxyBase base, IBaseResource resource, int httpCode, String reason) {
        FhirContext ctx = ResourceCache.ctx
//        String httpCodeString = EnglishReasonPhraseCatalog.INSTANCE.getReason(httpCode, Locale.ENGLISH)
        BasicHttpResponse outcome = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion('HTTP', 1, 1), httpCode, reason))
        String contentType = chooseContentType(base.clientContentTypes)
        outcome.addHeader('Content-Type', contentType)
        outcome.addHeader('Date', new HttpDateGenerator().currentDate)
        String content
        if (contentType.contains('json')) {
            content = ctx.newJsonParser().encodeResourceToString(resource)
        } else {
            content = ctx.newXmlParser().encodeResourceToString(resource)
        }
        outcome.setEntity(new StringEntity(content))
        return outcome

    }

    static chooseContentType(List<String> acceptableTypes) {
        if (acceptableTypes.empty)
            return 'application/fhir+json'
        def xx = types.intersect(acceptableTypes)
        if (xx) return xx[0]
        return 'application/fhir+json'
    }
}
