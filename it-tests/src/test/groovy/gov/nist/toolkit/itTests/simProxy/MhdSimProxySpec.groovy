package gov.nist.toolkit.itTests.simProxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import spock.lang.Shared
/**
 * Test SimProxy with MHD -> XDS transformation as front end to RegRepSpec simulator
 */
class MhdSimProxySpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared String patientId2 = 'BR15^^^&1.2.360&ISO'
    @Shared String envName = 'test'
    @Shared String testSession = 'bill';
    @Shared String id = 'rr'
    @Shared String fhirServer = "${testSession}__${id}"
    @Shared SimId simId = new SimId(fhirServer)  // ultimate destination
    @Shared String proxyId = "simproxy"
    @Shared String simProxyName = "${testSession}__${proxyId}"
    @Shared SimId simProxyId = new SimId(simProxyName)
    @Shared SimConfig rrConfig
    @Shared SimConfig proxySimConfig
    @Shared SimConfig updatedProxySimConfig
    @Shared TestInstance testInstance = new TestInstance('MhdSubmit')

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

//        api.deleteSimulatorIfItExists(simId)
//        api.deleteSimulatorIfItExists(simProxyId)

        api.createTestSession(testSession)

        // local customization

        new BuildCollections().init(null)

        spi.delete(ToolkitFactory.newSimId(id, testSession, ActorType.FHIR_SERVER.name, envName, true))
        spi.delete(ToolkitFactory.newSimId(proxyId, testSession, ActorType.SIM_PROXY.name, envName, true))
        spi.delete(proxyId, testSession)

        Installation.instance().defaultEnvironmentName()

        rrConfig = spi.create(
                id,
                testSession,
                SimulatorActorType.REPOSITORY_REGISTRY,
                envName)

        proxySimConfig = spi.create(
                proxyId,
                testSession,
                SimulatorActorType.SIM_PROXY,
                envName
        )

//        rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
//        spi.update(rrConfig)

        proxySimConfig.setProperty(SimulatorProperties.proxyForwardSite, fhirServer)

        // add MhdSubmissionTransformation to transforms
        List<String> transformations = proxySimConfig.asList(SimulatorProperties.simProxyTransformations)
        transformations.add('gov.nist.toolkit.simProxy.server.transforms.MhdSubmissionTransform')
        proxySimConfig.setProperty(SimulatorProperties.simProxyTransformations, transformations)

        updatedProxySimConfig = spi.update(proxySimConfig)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }

    def 'verify transformations installed' () {
        when:
        def xforms = updatedProxySimConfig.asList(SimulatorProperties.simProxyTransformations)

        then:
        xforms.size() ==1
        xforms.find { it == 'gov.nist.toolkit.simProxy.server.transforms.MhdSubmissionTransform' }
    }

    def 'send create through simproxy'() {
        when:
        def sections = ['submit']
        def params = [ :]
        List<Result> results = api.runTest(testSession, simProxyName, testInstance, sections, params, true)

        then:
        results.size() == 1
        results.get(0).passed()
    }

//    def 'send pnr and query through simproxy'() {
//        when:
//        SiteSpec siteSpec = new SiteSpec(simProxyName)
//        TestInstance testInstance = new TestInstance('12360')
//        List<String> sections = []
//        Map<String, String> params = new HashMap<>()
//        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");
//
//        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)
//
//        then:
//        testOverviewDTO.sections.get('submit').pass
//        testOverviewDTO.sections.get('query').pass
//        testOverviewDTO.pass
//    }
}
