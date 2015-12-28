package gov.nist.toolkit.xcTests
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
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
    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", port)
    SimulatorBuilder spi = new SimulatorBuilder(urlRoot)
//    EngineSpi spi = new EngineSpi(String.format('http://localhost:%s/xdstools2', port));
    @Shared server
    BasicSimParameters RGParams = new BasicSimParameters();
    BasicSimParameters IGParams = new BasicSimParameters();
    String patientId = 'BR14^^^&1.2.360&ISO'
    String testSession = 'mike'
    @Shared  apiEnvironment = 'test'
    @Shared  spiEnvironment = 'test'
    TestInstance testId
    List<String> sections
    Map<String, String> qparams
    boolean stopOnFirstError = true
    List<Result> results
    String RGSiteName = 'mike__rg1'
    String IGSiteName = 'mike__ig'


    def setupSpec() {   // one time setup done when class launched
        TestSession.setupToolkit()
        api = ToolkitApi.forServiceUse()

        server = new GrizzlyController()
        server.start(port);
        server.withToolkit()
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
    }

    def setup() {  // run before each test method
        RGParams.id = 'rg1'
        RGParams.user = testSession
        RGParams.actorType = SimulatorActorType.RESPONDING_GATEWAY
        RGParams.environmentName = spiEnvironment

        IGParams.id = 'ig'
        IGParams.user = testSession
        IGParams.actorType = SimulatorActorType.INITIATING_GATEWAY
        IGParams.environmentName = spiEnvironment
    }

    def 'Test Responding Gateway' () {
        when:
        println 'STEP - DELETE RESPONDING GATEWAY SIM'
        spi.delete(RGParams.id, RGParams.user)

        and:
        println 'STEP - CREATE RESPONDING GATEWAY SIM'
        SimId RGSimId = spi.create(
                RGParams.id,
                RGParams.user,
                RGParams.actorType,
                RGParams.environmentName
        )

        then: 'verify sim built'
        RGSimId.getId() == RGParams.id

        when: 'disable checking of Patient Identity Feed checking'
        SimConfig RGConfig = spi.get(RGSimId)
        RGConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        spi.update(RGConfig)

        and: 'Submit one Document to Rep/Reg behind RG'
//        String testSession = testSession;  // use default
        testId = new TestInstance("12318")   //11966
        sections = null
        qparams = new HashMap<>()
        qparams.put('$patientid$', patientId)

        and: 'Run Register test'
        results = api.runTest(testSession, RGSiteName, testId, sections, qparams, stopOnFirstError)

        then:  'verify register worked'
        results.size() == 1
        results.get(0).passed()

        when: 'cross community query to RG to verify test data'
        testId = new TestInstance("12310")
        sections = null
        qparams = new HashMap<>()
        qparams.put('$patientid$', patientId)

        and: 'Run xcq test'
        results = api.runTest(testSession, RGSiteName, testId, sections, qparams, stopOnFirstError)

        then:  'verify query worked'
        results.size() == 1
        results.get(0).passed()

        when:
        println 'STEP - DELETE INITIATING GATEWAY SIM'
        spi.delete(IGParams.id, IGParams.user)

        and:
        println 'STEP - CREATE INITIATING GATEWAY SIM'
        SimId IGSimId = spi.create(
                IGParams.id,
                IGParams.user,
                IGParams.actorType,
                IGParams.environmentName
        )

        then: 'verify sim built'
        IGSimId.getId() == IGParams.id

        when: 'link ig to rg'
        SimConfig IGConfig = spi.get(IGSimId)
        IGConfig.setProperty(SimulatorProperties.respondingGateways, [ RGSimId.getFullId() ])
        SimConfig updatedIGConfig = spi.update(IGConfig)

        then:
        updatedIGConfig.asList(SimulatorProperties.respondingGateways) == [ RGSimId.getFullId() ]

//        when: 'send stored query to IG'

    }

}
