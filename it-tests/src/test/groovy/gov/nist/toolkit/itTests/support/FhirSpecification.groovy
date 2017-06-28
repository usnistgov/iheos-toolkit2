package gov.nist.toolkit.itTests.support

import gov.nist.toolkit.grizzlySupport.GrizzlyController
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
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

    String baseURL() { "http://localhost:${remoteToolkitPort}/xdstools2/fsim"}

    String baseURL(SimId simId) { "${baseURL()}/${simId.toString()}"}

    def post(def uri,  def _body) {
        HttpClient httpclient = HttpClients.createDefault()
        HttpPost post = new HttpPost(uri)
        HttpEntity entity = new StringEntity(_body)
        entity.contentType = 'application/fhir+json'
        post.setEntity(entity)
        HttpResponse response = httpclient.execute(post)
        FhirId locationHeader
        String lhdr = response.getFirstHeader('Location')
        if (lhdr) {
            def (nametag, value) = lhdr.split(':')
            locationHeader = new FhirId(value)
        }
        HttpEntity entity2
        try {
            entity2 = response.entity
            def statusLine = response.statusLine

            InputStream is = entity2.getContent()
            String content = Io.getStringFromInputStream(is)

            return [statusLine, content, locationHeader]
        } finally {
            response.close()
        }
    }

    def get(def uri) {
        HttpClient client = HttpClientBuilder.create().build()
        HttpGet request = new HttpGet(uri)
        request.addHeader('Content-Type', 'application/fhir+json')
        HttpResponse response = client.execute(request)
        return [response.statusLine, Io.getStringFromInputStream(response.getEntity().content)]
    }

}
