package gov.nist.toolkit.fhir.server.utility

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.server.resourceMgr.FileSystemResourceCache
import org.apache.http.HttpResponse
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

    static BasicHttpResponse wrap(String contentType, IBaseResource resource, int httpCode) {
        wrap(contentType, resource, httpCode, EnglishReasonPhraseCatalog.INSTANCE.getReason(httpCode, Locale.ENGLISH))
    }


    static BasicHttpResponse wrap(String contentType, IBaseResource resource, int httpCode, String reason) {
        BasicHttpResponse outcome = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion('HTTP', 1, 1), httpCode, reason))
        inResponse(outcome, contentType, resource)
        return outcome
    }

    static BasicHttpResponse wrap(String contentType, byte[] content, int httpCode, String reason) {
        BasicHttpResponse outcome = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion('HTTP', 1, 1), httpCode, reason))
        inResponse(outcome, contentType, new String(content))
        return outcome
    }

    static void inResponse(HttpResponse response, String contentType, IBaseResource resource) {
        FhirContext ctx = FileSystemResourceCache.ctx
        String content
        if (contentType.contains('json')) {
            content = ctx.newJsonParser().encodeResourceToString(resource)
        } else {
            content = ctx.newXmlParser().encodeResourceToString(resource)
        }
        inResponse(response, contentType, content)
    }

    static void inResponse(HttpResponse response, String contentType, String content) {
        response.addHeader('Content-Type', contentType)
        response.addHeader('Date', new HttpDateGenerator().currentDate)
        response.setEntity(new StringEntity(content))
    }
}
