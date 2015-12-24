package gov.nist.toolkit.xcTests

import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
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
    @Shared ToolkitApi api;
    def host='localhost'
    @Shared def port = '8889'
    EngineSpi builder = new EngineSpi(String.format('http://localhost:%s/xdstools2', port));
    @Shared HttpServer server
    BasicSimParameters params = new BasicSimParameters();
    String patientId = 'BR14^^^&1.2.360&ISO'
    String testSession = 'mike'

    def setupGrizzly() {
        server = new GrizzlyController().startServer(port);
    }

    def setupSpec() {   // one time setup done when class launched
        TestSession.setupToolkit()
//        api = ToolkitApi.forServiceUse()
        api = ToolkitApi.forInternalUse()
        setupGrizzly()
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.shutdownNow()
    }

    def setup() {  // run before each test method
        params.id = 'reg'
        params.user = testSession
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

        when: 'Submit one Document to Rep/Reg behind RG'
        String testSession = testSession;  // use default
        String siteName = 'mike__reg'
        TestInstance testId = new TestInstance("11996")
        List<String> sections = ['submit']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run Register test'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

}
