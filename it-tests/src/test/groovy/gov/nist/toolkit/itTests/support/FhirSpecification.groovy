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

    class LocationHeader {
        String id = null   // id
        String vid = null  // version
    }


    def post(def uri,  def _body) {
        HttpClient httpclient = HttpClients.createDefault()
        HttpPost post = new HttpPost(uri)
        HttpEntity entity = new StringEntity(_body)
        entity.contentType = 'application/fhir+json'
        post.setEntity(entity)
        HttpResponse response = httpclient.execute(post)
        LocationHeader locationHeader = new LocationHeader()
        String lhdr = response.getFirstHeader('Location')
        if (lhdr) {
            def (nametag, value) = lhdr.split(':')
            def locationParts = value.split('/')
            int historyIndex = locationParts.findIndexOf { it == '_history'}
            if (historyIndex != -1) {
                locationHeader.id = locationParts[historyIndex - 1]
                locationHeader.vid = locationParts[historyIndex + 1]
            } else {
                locationHeader.id = locationParts[locationParts.size()-1]
            }
        }
        HttpEntity entity2
        try {
            entity2 = response.entity
            def statusLine = response.statusLine
//            println "status line : ${statusLine}"
//            println entity2.getClass().getName()

            InputStream is = entity2.getContent()
            String content = Io.getStringFromInputStream(is)

            return [statusLine, content, locationHeader]
        } finally {
            response.close()
        }
        return null
    }

}
