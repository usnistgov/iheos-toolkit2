package gov.nist.toolkit.xcTests

import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.EngineSpi
import gov.nist.toolkit.toolkitServicesCommon.SimId
import org.glassfish.grizzly.http.server.HttpServer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Build RG, initialize it with a single submission, query and retreive the submission
 */
class XcQueryTest extends Specification {
    def host='localhost'
    @Shared def port = '8889'
    EngineSpi builder = new EngineSpi(String.format('http://localhost:%s/xdstools2', port));
    @Shared HttpServer server
    BasicSimParameters params = new BasicSimParameters();

    def setupGrizzly() {
        server = new GrizzlyController().startServer(port);
    }

    def setupSpec() {   // one time setup done when class launched
        TestSession.setupToolkit()
        ToolkitApi.forServiceUse()
        setupGrizzly()
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.shutdownNow()
    }

    def setup() {  // run before each test method
        params.id = 'reg'
        params.user = 'mike'
        params.actorType = SimulatorActorType.RESPONDING_GATEWAY
        params.environmentName = 'test'
    }

    def 'Create Responding Gateway' () {
        when:
        println 'STEP - DELETE RESPONDING GATEWAY SIM'
        builder.delete(params.id, params.user)

        and:
        println 'STEP - CREATE RESPONDING GATEWAY SIM'
        SimId simId = builder.create(
                params.id,
                params.user,
                params.actorType,
                params.environmentName
        )

        then: 'verify sim built'
        simId.getId() == params.id
    }
}
