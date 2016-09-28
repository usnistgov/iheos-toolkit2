package gov.nist.toolkit.itTests.xds.rep

import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RepOrchestrationRequest
import gov.nist.toolkit.services.client.RepOrchestrationResponse
import gov.nist.toolkit.services.server.orchestration.RepOrchestrationBuilder
import gov.nist.toolkit.session.client.TestOverviewDTO
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared

/**
 *
 */
class RepSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi
    @Shared String testSession = 'repspec';
    @Shared String id = 'rep'
    @Shared SimId simId = new SimId(testSession, id)
    @Shared String envName = 'default'
    @Shared SimConfig repSimConfig


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        if (spi.get(id, testSession))
            spi.delete(id, testSession)

        api.deleteSimulatorIfItExists(simId)

        repSimConfig = spi.create(
                id,
                testSession,
                SimulatorActorType.REPOSITORY,
                envName)

        api.createTestSession(testSession)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        spi.delete(id, testSession)
        api.deleteSimulatorIfItExists(simId)
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
    }

    def '12360' () {
        setup:
        RepOrchestrationRequest request = new RepOrchestrationRequest()
        request.environmentName = envName
        request.userName = testSession
        request.useExistingSimulator = false
        request.sutSite = new SiteSpec(simId.toString())

        when: 'build orchestration'
        def builder = new RepOrchestrationBuilder(api, session, request)
        RawResponse rawResponse = builder.buildTestEnvironment()

        then:
        rawResponse instanceof RepOrchestrationResponse

        when:
        RepOrchestrationResponse response = (RepOrchestrationResponse) rawResponse
        SimulatorConfig supportConfig = response.regConfig

        then: 'orchestration completed successfully'
        !response.isError()
        supportConfig.getConfigEle(SimulatorProperties.registerEndpoint) != null
        supportConfig.getConfigEle(SimulatorProperties.storedQueryEndpoint) != null

        when: 'configure repository SUT to forward to registry built by orchestration'
        repSimConfig.setProperty(SimulatorProperties.registerEndpoint, supportConfig.getConfigEle(SimulatorProperties.registerEndpoint).asString())
        repSimConfig = spi.update(repSimConfig)

        and: 'run test'
        SiteSpec siteSpec = response.repSite
        siteSpec.orchestrationSiteName = supportConfig.id

        SimManager simManager = new SimManager(api.getSession().id)
        Sites sites = simManager.getAllSites(new Sites())
        Site sutSite = sites.getSite(siteSpec.name)

        TestInstance testInstance = new TestInstance('12360')
        List<String> sections = []
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");

        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.pass
    }

}
