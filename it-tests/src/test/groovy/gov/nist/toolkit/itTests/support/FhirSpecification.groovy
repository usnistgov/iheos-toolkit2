package gov.nist.toolkit.itTests.support

import gov.nist.toolkit.fhir.server.utility.FhirClient
import gov.nist.toolkit.grizzlySupport.GrizzlyController
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory

/**
 *
 */
class FhirSpecification extends ToolkitSpecification {

    def startGrizzlyWithFhir(String port) {
        remoteToolkitPort = port
//        server = new GrizzlyController()
//        server.start(remoteToolkitPort);
//        server.withFhirServlet()
//        Installation.instance().overrideToolkitPort(remoteToolkitPort)  // ignore toolkit.properties
    }

    String baseURL() { "http://localhost:${remoteToolkitPort}/xdstools2/fsim"}

    String baseURL(SimId simId) { "${baseURL()}/${simId.toString()}"}

    /**
     * Send an HTTP POST
     * @param uri  URI
     * @param _body anything that evaluates to a string
     * @return  [ HttpResponse.StatusLine, String contentReturned, String HTTP Location header]
     */
    def post(def uri,  def _body) { FhirClient.post(uri, _body) }

    /**
     * send an HTTP GET
     * @param uri URI
     * @return [ HttpResponse.StatusLine, String contentReturned ]
     */
    def get(def uri) { FhirClient.get(uri) }

    /**
     * create SPI variety of SimId
     * @param simId
     */
    gov.nist.toolkit.toolkitServicesCommon.SimId spiSimId(SimId simId) {
        ToolkitFactory.newSimId(simId.id, simId.testSession.value, simId.actorType, simId.environmentName, simId.fhir)
    }


}
