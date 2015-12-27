package gov.nist.toolkit.xcTests

import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.EngineSpi
import gov.nist.toolkit.toolkitServicesCommon.SimId
import spock.lang.Shared
import spock.lang.Specification
/**
 * Build RG, initialize it with a single submission, query and retreive the submission.
 *
 * This test uses two copies of toolkit.  One copy is launched via Grizzly and offers the
 * simulator services.  It is references through the variable spi.
 *
 * The second copy is referenced as the testclient and is run this is thread.  It is initialized
 * and referenced through the variable api.
 */
class XcQueryTest extends Specification {
    @Shared ToolkitApi api;
    def host='localhost'
    @Shared def port = '8889'
    EngineSpi spi = new EngineSpi(String.format('http://localhost:%s/xdstools2', port));
    @Shared server
    BasicSimParameters params = new BasicSimParameters();
    String patientId = 'BR14^^^&1.2.360&ISO'
    String testSession = 'mike'
    @Shared  apiEnvironment = 'test'
    @Shared  spiEnvironment = 'test'

    def setupGrizzly() {
        server = new GrizzlyController().start(port);
    }

    def setupSpec() {   // one time setup done when class launched
        TestSession.setupToolkit(apiEnvironment)

        api = ToolkitApi.forServiceUse()
//        api = ToolkitApi.forInternalUse()

        setupGrizzly()
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
    }

    def setup() {  // run before each test method
        params.id = 'reg'
        params.user = testSession
        params.actorType = SimulatorActorType.RESPONDING_GATEWAY
        params.environmentName = spiEnvironment
    }

    def 'Create Responding Gateway' () {
        when:
//        println 'STEP - DELETE RESPONDING GATEWAY SIM'
//        spi.delete(params.id, params.user)
//
//        and:
        println 'STEP - CREATE RESPONDING GATEWAY SIM'
        SimId simId = spi.create(
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
        TestInstance testId = new TestInstance("11966")
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
