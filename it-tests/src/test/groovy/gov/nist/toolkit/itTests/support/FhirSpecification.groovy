package gov.nist.toolkit.itTests.support

import gov.nist.toolkit.grizzlySupport.GrizzlyController
import gov.nist.toolkit.installation.Installation
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
/**
 *
 */
class FhirSpecification extends ToolkitSpecification {

    @Override
    def startGrizzly(String port) {
        remoteToolkitPort = port
        server = new GrizzlyController()
        server.start(remoteToolkitPort);
        server.withFhirServlet()
        Installation.instance().overrideToolkitPort(remoteToolkitPort)  // ignore toolkit.properties
    }

    String post(def uri,  def _body) {
        CloseableHttpClient httpclient = HttpClients.createDefault()
        HttpPost post = new HttpPost(uri)
        OutputStream baos
        HttpEntity entity = new StringEntity(_body)
        entity.contentType = 'application/fhir+json'
//        HttpEntity() {
//            @Override
//            boolean isRepeatable() {
//                return true
//            }
//
//            @Override
//            boolean isChunked() {
//                return true
//            }
//
//            @Override
//            long getContentLength() {
//                return -1
//            }
//
//            @Override
//            Header getContentType() {
//                return new BasicHeader('Content-type','application/fhir+json')
//            }
//
//            @Override
//            Header getContentEncoding() {
//                return null
//            }
//
//            @Override
//            InputStream getContent() throws IOException, UnsupportedOperationException {
//                return Io.stringToInputStream(_body)
//            }
//
//            @Override
//            void writeTo(OutputStream outputStream) throws IOException {
//                baos = outputStream
//            }
//
//            @Override
//            boolean isStreaming() {
//                return false
//            }
//
//            @Override
//            void consumeContent() throws IOException {
//
//            }
//        }
        post.setEntity(entity)
        CloseableHttpResponse response = httpclient.execute(post)
        HttpEntity entity2
        def statusLine = null
        try {
            statusLine = response.statusLine
            entity2 = response.entity
        } finally {
            response.close()
        }
        println "status line : ${statusLine}"
    }

}
