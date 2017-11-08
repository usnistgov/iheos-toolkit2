package gov.nist.toolkit.fhir.utility

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.ExceptionUtil
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.ProtocolVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicStatusLine
import org.apache.log4j.Logger
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class FhirClient {
    static private final Logger logger = Logger.getLogger(FhirClient.class);

    /**
     * Send an HTTP POST
     * @param uri  URI
     * @param _body anything that evaluates to a string
     * @return  [ BasicStatusLine, String contentReturned, String HTTP Location header]
     */
    static post(def uri,  def _body) {
        HttpResponse response
        try {
            HttpClient httpclient = HttpClients.createDefault()
            HttpPost post = new HttpPost(uri)
            HttpEntity entity = new StringEntity(_body)
            entity.contentType = 'application/fhir+json'
            post.setEntity(entity)
            response = httpclient.execute(post)
            FhirId locationHeader
            String lhdr = response.getFirstHeader('Location')
            if (lhdr) {
                def (nametag, value) = lhdr.split(':')
                locationHeader = new FhirId(value)
            }
            HttpEntity entity2
            entity2 = response.entity
            def statusLine = response.statusLine

            InputStream is = entity2.getContent()
            String content = Io.getStringFromInputStream(is)

            return [statusLine, content, locationHeader]
        } catch (Exception e) {
            logger.error(ExceptionUtil.exception_details(e))
            BasicStatusLine statusLine = new BasicStatusLine(new ProtocolVersion('http', 1, 1), 400, e.getMessage())
            return [statusLine, null, null]
        } finally {
            if (response)
                response.close()
        }
    }

    /**
     * send an HTTP GET
     * @param uri URI
     * @return [ BasicStatusLine, String contentReturned ]
     */
    static get(def uri) {
        HttpClient client = HttpClientBuilder.create().build()
        HttpGet request = new HttpGet(uri)
        request.addHeader('Content-Type', 'application/fhir+json')
        HttpResponse response = client.execute(request)
        def statusLine = response.getStatusLine()
        return [statusLine, Io.getStringFromInputStream(response.getEntity().content)]
    }

    static IBaseResource readResource(def uri) {
        try {
            def (statusLine, body) = get(uri)
            if (statusLine.statusCode != 200)
                return null
            return ToolkitFhirContext.get().newJsonParser().parseResource(body)
        }
        catch (Exception e) {
            return null
        }
    }

}
