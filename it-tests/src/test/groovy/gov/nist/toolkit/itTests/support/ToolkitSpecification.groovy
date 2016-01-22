package gov.nist.toolkit.itTests.support

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.grizzlySupport.GrizzlyController
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import spock.lang.Shared
import spock.lang.Specification

/**
 *
 */

class ToolkitSpecification extends Specification {
    // these are usable by the specification that extend this class
    @Shared GrizzlyController server = null
    @Shared ToolkitApi api
    @Shared Session session
    @Shared String remoteToolkitPort = null

    def setupSpec() {  // there can be multiple setupSpec() fixture methods - they all get run
        session = UnitTestEnvironmentManager.setupLocalToolkit()
        api = UnitTestEnvironmentManager.localToolkitApi()
    }

    def startGrizzly(String port) {
        remoteToolkitPort = port
        server = new GrizzlyController()
        server.start(remoteToolkitPort);
        server.withToolkit()
        Installation.installation().overrideToolkitPort(remoteToolkitPort)  // ignore toolkit.properties

    }

    SimulatorBuilder getSimulatorApi(String remoteToolkitPort) {
        String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
        new SimulatorBuilder(urlRoot)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        if (server) {
            server.stop()
            server = null
        }
        ListenerFactory.terminateAll()
    }

}
