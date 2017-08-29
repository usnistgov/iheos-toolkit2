package gov.nist.toolkit.itTests.simProxy

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 *
 */
class RegRep extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared String patientId2 = 'BR15^^^&1.2.360&ISO'
    @Shared String envName = 'test'
    @Shared String testSession = 'bill';
    @Shared String id = 'regrep'
    @Shared String regrep = "${testSession}__${id}"
    @Shared SimId simId = new SimId(regrep)  // ultimate destination
    @Shared String proxyId = "simproxy"
    @Shared String simProxyName = "${testSession}__${proxyId}"
    @Shared SimId simProxyId = new SimId(simProxyName)
    @Shared SimConfig regrepSimConfig
    @Shared SimConfig proxySimConfig

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        api.deleteSimulatorIfItExists(simId)
        api.deleteSimulatorIfItExists(simProxyId)

        api.createTestSession(testSession)

        // local customization

        new BuildCollections().init(null)

        spi.delete(id, testSession)
        spi.delete(proxyId, testSession)

        Installation.instance().defaultEnvironmentName()

        regrepSimConfig = spi.create(
                id,
                testSession,
                SimulatorActorType.REPOSITORY_REGISTRY,
                envName)

        proxySimConfig = spi.create(
                'simproxy',
                testSession,
                SimulatorActorType.SIM_PROXY,
                envName
        )

        regrepSimConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        spi.update(regrepSimConfig)

        proxySimConfig.setProperty(SimulatorProperties.proxyForwardSite, regrep)
        spi.update(proxySimConfig)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
//        api.createTestSession(testSession)
//        if (!api.simulatorExists(simId)) {
//            println "Creating sim ${simId}"
//            api.createSimulator(ActorType.REGISTRY, simId)
//        }
//
//        if (!api.simulatorExists(simProxyId)) {
//            println "Creating sim ${simProxyId}"
//            api.createSimulator(ActorType.SIM_PROXY, simProxyId)
//        }
    }

    def 'send pnr through simproxy'() {
        when:
        SiteSpec siteSpec = new SiteSpec(simProxyName)
        TestInstance testInstance = new TestInstance('12360')
        List<String> sections = ['submit']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");

        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass
    }

    def 'send pnr and query through simproxy'() {
        when:
        SiteSpec siteSpec = new SiteSpec(simProxyName)
        TestInstance testInstance = new TestInstance('12360')
        List<String> sections = []
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");

        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass
        testOverviewDTO.sections.get('query').pass
        testOverviewDTO.pass
    }
}
