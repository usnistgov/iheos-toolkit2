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

    HttpEntity post(def uri,  def _body) {
        CloseableHttpClient httpclient = HttpClients.createDefault()
        HttpPost post = new HttpPost(uri)
        OutputStream baos
        HttpEntity entity = new StringEntity(_body)
        entity.contentType = 'application/fhir+json'
        post.setEntity(entity)
        CloseableHttpResponse response = httpclient.execute(post)
        HttpEntity entity2
        try {
            entity2 = response.entity
            def statusLine = response.statusLine
            println "status line : ${statusLine}"

//            Header contentEncodingHeader = entity2.getContentEncoding();
//
//            if (contentEncodingHeader != null) {
//                HeaderElement[] encodings =contentEncodingHeader.getElements();
//                for (int i = 0; i < encodings.length; i++) {
//                    if (encodings[i].getName().equalsIgnoreCase("gzip")) {
//                        entity2 = new GzipDecompressingEntity(entity2);
//                        break;
//                    }
//                }
//            }

            println entity2.getClass().getName()
            return entity2
        } finally {
            response.close()
        }
        return null
    }

}
