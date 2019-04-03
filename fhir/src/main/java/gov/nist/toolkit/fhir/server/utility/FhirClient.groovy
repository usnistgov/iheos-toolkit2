package gov.nist.toolkit.fhir.server.utility

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
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class FhirClient implements IFhirSearch {
    static private final Logger logger = Logger.getLogger(FhirClient.class);

    /**
     * Send an HTTP POST
     * @param uri  URI
     * @param _body anything that evaluates to a string
     * @return  [ BasicStatusLine, String contentReturned, String HTTP Location header]
     */
    static post(def uri,  def _body) {
        HttpResponse response
        def error = null
        try {
            String contentType = contentType(_body)
            HttpClient httpclient = HttpClients.createDefault()
            HttpPost post = new HttpPost(uri)
            HttpEntity entity = new StringEntity(_body)
            entity.contentType = contentType
            post.setHeader('Accept', contentType)
            post.setEntity(entity)
            response = httpclient.execute(post)

            def (responseContentType, theError) = isolateContentType(response.getFirstHeader('Content-Type') as String, contentType)
            error = theError

            FhirId locationHeader = null
            String lhdr = response.getFirstHeader('Location')
            if (lhdr) {
                def (nametag, value) = lhdr.split(':', 2)
                locationHeader = new FhirId(value)
            }
            HttpEntity entity2
            entity2 = response.entity
            def statusLine = response.statusLine

            InputStream is = entity2.getContent()
            String content = Io.getStringFromInputStream(is)

            return [statusLine, content, locationHeader, error]
        } catch (Exception e) {
            logger.error(ExceptionUtil.exception_details(e))
            BasicStatusLine statusLine = new BasicStatusLine(new ProtocolVersion('http', 1, 1), 400, e.getMessage())
            return [statusLine, null, null, null]
        } finally {
            if (response)
                response.close()
        }
    }

    // returns [ contentType, errorMsg ]
    static def isolateContentType(String contentTypeHeader, String expectedValue) {
        def error = null
        def contentType
        if (contentTypeHeader && contentTypeHeader.contains(':')) {  // header name included
            contentType = contentTypeHeader.split(':')[1].trim()
        } else {
            contentType = contentTypeHeader
        }

        if (contentType && contentType.contains(';')) {  // has params
            contentType = contentType.split(';')[0].trim()
        }
        if (!contentType)  // not all responses have Content-Type
            return [ '', null ]
        if (contentType != expectedValue) {
            error = "Expected Content-Type ${expectedValue}\nFound ${contentType} in ${contentTypeHeader} instead"
        }
        [ contentType, error ]
    }

    static String contentType(String content) {
        if (isJson(content)) return 'application/fhir+json'
        return 'application/fhir+xml'
    }

    static boolean isJson(String content) {
        if (content == null) return true;
        if (content.trim().startsWith('{')) return true;
        return false;
    }

    /**
     * send an HTTP GET
     * @param uri URI
     * @return [ BasicStatusLine, String contentReturned ]
     */
    static get(def uri) {
        get(uri, 'application/fhir+json')
    }


    static get(def uri, def contentType) {
        try {
            HttpClient client = HttpClientBuilder.create().build()
            HttpGet request = new HttpGet(uri)
            request.addHeader('Accept', contentType)
            logger.info("GET ${uri} for content type ${contentType}")
            HttpResponse response = client.execute(request)
            def statusLine = response.getStatusLine()
            logger.info("==> ${statusLine.statusCode}, ${statusLine.reasonPhrase}")
            return [statusLine, Io.getStringFromInputStream(response.getEntity().content)]
        }
        catch (Throwable e) {
            logger.error("GET from ${uri} for content type ${contentType} failed: ${e.getMessage()}")
            throw new Exception("GET from ${uri} for content type ${contentType} failed.", e)
        }
    }

    static getBytes(def uri, def contentType) {
        try {
            HttpClient client = HttpClientBuilder.create().build()
            HttpGet request = new HttpGet(uri)
            request.addHeader('Accept', contentType)
            HttpResponse response = client.execute(request)
            def returnContentType = response.getHeaders('Content-Type')[0].value
            def statusLine = response.getStatusLine()
            logger.info("GET ${uri} for content type ${contentType}")
            return [statusLine, returnContentType, Io.getBytesFromInputStream(response.getEntity().content)]
        }
        catch (Throwable e) {
            logger.error("GET from ${uri} for content type ${contentType} failed: ${e.getMessage()}")
            throw new Exception("GET from ${uri} for content type ${contentType} failed.", e)
        }
    }

    static IBaseResource readResource(def uri) {
        readResource(uri, 'application/fhir+json')
    }

    static IBaseResource readResource(def uri, String contentType) {
        def (statusLine, body) = get(uri, contentType)
        boolean isJson = contentType.contains('json')
        if (isJson)
            return ToolkitFhirContext.get().newJsonParser().parseResource(body)
        else
            return ToolkitFhirContext.get().newXmlParser().parseResource(body)
    }

    /**
     *
     * @param base  FHIR server base URL
     * @param resourceType "Patient" for example
     * @param params List of "name=value"
     * @return  fullUrl ==> Resource
     */
    Map<URI, IBaseResource> search(String base, String resourceType, List<String> params) {
        try {
            def url = buildURL(base, resourceType, params)
            IBaseResource theBundle = readResource(url)
            if (theBundle instanceof Bundle) {
                Bundle bundle = theBundle
                logger.info("...returning ${bundle.entry.size()} entries")
                Map<URI, IBaseResource> map = [:]
                bundle.entry.each { Bundle.BundleEntryComponent comp ->
                    def fullUrl = comp.fullUrl
                    IBaseResource resource = comp.getResource()
                    map[UriBuilder.build(fullUrl)] = resource
                }
                return map
            }
            Map<URI, IBaseResource> map = [:]
            map[new URI('')] = theBundle
            return map
//            throw new Exception("returned resource of type ${theBundle.class.name}")
        }
        catch (Throwable e) {
            logger.error("...${e.getMessage()}")
            throw e
        }
    }

    static URI buildURL(String base, String resourceType, List params) {
        def path = "${base}/${resourceType}?${params.join(';')}"
       URI uri = UriBuilder.build(path)
        return uri
    }

}
