package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RigOrchestrationRequest
import gov.nist.toolkit.services.client.RigOrchestrationResponse
import gov.nist.toolkit.services.server.orchestration.RigOrchestrationBuilder
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Integration tests for RIG Simulator
 */
class RigSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi
    @Shared TestSession testSession = new TestSession('rigspec')
    @Shared String id = 'simulator_rig'
    @Shared SimId simId = new SimId(testSession, id)
    @Shared String envName = 'default'
    @Shared SimConfig sutSimConfig


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)

    /**
     * Run once at class initialization.
     * @return
     */
    def setupSpec() {
        // Opens a grizzly server for testing, in lieu of tomcat
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // builds his test collections. mimics startup of tomcat
        new BuildCollections().init(null)

        // creates the special testSession/session for this test
        api.createTestSession(testSession.value)
    }

    // one time shutdown when everything is done
    def cleanupSpec() {
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
    }

    def 'rig tests' () {
        setup: 'rig orchestration request is set up'
        RigOrchestrationRequest request = new RigOrchestrationRequest()
        request.environmentName = envName
        request.testSession = testSession
        request.useExistingSimulator = false
        request.siteUnderTest = new SiteSpec(simId.id, simId.testSession)

        when: 'build orchestration'
        def builder = new RigOrchestrationBuilder(api, session, request)
        RawResponse rawResponse = builder.buildTestEnvironment()

        then: 'return is instance of RigOrchestrationResponse'
        rawResponse != null
        rawResponse instanceof RigOrchestrationResponse

        when: 'cast response to RigOrchestrationResponse'
        RigOrchestrationResponse response = (RigOrchestrationResponse) rawResponse

        then: 'orchestration completed successfully'
        !response.isError()

        when: 'run test'
        SiteSpec siteSpec = request.siteUnderTest

        SimManager simManager = new SimManager(api.getSession().id)
        Sites sites = simManager.getAllSites(new Sites(testSession), testSession)
//        Site sutSite = sites.getSite(siteSpec.name, new TestSession(testSession))

        TestInstance testInstance = new TestInstance(testId)
        List<String> sections = []
        Map<String, String> params = new HashMap<>()

        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then: 'Returned test status is pass'
        testOverviewDTO.pass

        where: "tests to run include:"
        testId         || valid
        "rig_5420"     || true
        "rig_5421"     || true
        "rig_5422"     || true
        "rig_5423"     || true
        "rig_5424"     || true
        "rig_5425"     || true
        "rig_5426"     || true
        "rig_5427"     || true
        "rig_5428"     || true
        "rig_5429"     || true
        "rig_5430"     || true
    }

}
