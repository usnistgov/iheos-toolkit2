package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.IdsOrchestrationRequest
import gov.nist.toolkit.services.client.IdsOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.orchestration.IdsOrchestrationBuilder
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
 * Integration tests for IDS Simulator
 */
class IdsSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi
    @Shared String testSession = 'idsspec';
    @Shared String id = 'simulator_ids'
    @Shared SimId simId = new SimId(new TestSession(testSession), id)
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
        api.createTestSession(testSession)
    }

    // one time shutdown when everything is done
    def cleanupSpec() {
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
    }

    def 'ids tests' () {
        setup: 'ids orchestration request is set up'
        IdsOrchestrationRequest request = new IdsOrchestrationRequest()
        request.environmentName = envName
        request.testSession = new TestSession(testSession)
        request.useExistingSimulator = false
        request.siteUnderTest = new SiteSpec(id, new TestSession(testSession))

        when: 'build orchestration'
        def builder = new IdsOrchestrationBuilder(api, session, request)
        RawResponse rawResponse = builder.buildTestEnvironment()

        then: 'return is instance of IdsOrchestrationResponse'
        rawResponse != null
        rawResponse instanceof IdsOrchestrationResponse

        when: 'cast response to IdsOrchestrationResponse'
        IdsOrchestrationResponse response = (IdsOrchestrationResponse) rawResponse

        then: 'orchestration completed successfully'
        !response.isError()

        when: 'run test'
        SiteSpec siteSpec = request.siteUnderTest

        SimManager simManager = new SimManager(api.getSession().id)
        Sites sites = simManager.getAllSites(new Sites(new TestSession(testSession)), new TestSession(testSession))
//        Site sutSite = sites.getSite(siteSpec.name, new TestSession(testSession))

        TestInstance testInstance = new TestInstance(testId)
        testInstance.testSession = new TestSession(testSession)
        List<String> sections = []
        Map<String, String> params = new HashMap<>()

        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, new TestSession(testSession), siteSpec, testInstance, sections, params, null, true)

        then: 'Returned test status is pass'
        testOverviewDTO.pass

        where: "tests to run include:"
        testId         || valid
        "ids_4805"     || true
        "ids_4806"     || true
        "ids_4809"     || true
        "ids_4810"     || true
        "ids_4811"     || true
        "ids_4812"     || true
        "ids_4820"     || true
    }

}
