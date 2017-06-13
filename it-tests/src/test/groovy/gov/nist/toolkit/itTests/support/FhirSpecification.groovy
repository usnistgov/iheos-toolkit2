package gov.nist.toolkit.itTests.support

import gov.nist.toolkit.grizzlySupport.GrizzlyController
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
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

    List<String> post(def uri,  def _body) {
        HttpClient httpclient = HttpClients.createDefault()
        HttpPost post = new HttpPost(uri)
        HttpEntity entity = new StringEntity(_body)
        entity.contentType = 'application/fhir+json'
        post.setEntity(entity)
        HttpResponse response = httpclient.execute(post)
        HttpEntity entity2
        try {
            entity2 = response.entity
            def statusLine = response.statusLine
            println "status line : ${statusLine}"

            println entity2.getClass().getName()

            InputStream is = entity2.getContent()
            String content = Io.getStringFromInputStream(is)

            return [statusLine, content]
        } finally {
            response.close()
        }
        return null
    }

}
