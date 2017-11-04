package gov.nist.toolkit.itTests.simProxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 *
 */
class RecipientSpecxx extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared String patientId2 = 'BR15^^^&1.2.360&ISO'
    @Shared String envName = 'test'
    @Shared String testSession = 'bill';
    @Shared String id = 'regrep'
    @Shared String rr = "${testSession}__${id}"
    @Shared SimId simId = new SimId(rr)  // ultimate destination
    @Shared String proxyId = "simproxy"
    @Shared String simProxyName = "${testSession}__${proxyId}"
    @Shared SimId simProxyId = new SimId(simProxyName)
    @Shared SimConfig rrSimConfig
    @Shared SimConfig proxySimConfig
    @Shared SimConfig updatedProxySimConfig

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
        spi.delete(proxyId + '_be', testSession)

        Installation.instance().defaultEnvironmentName()

        rrSimConfig = spi.create(
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

        proxySimConfig.setProperty(SimulatorProperties.proxyForwardSite, rr)
        List<String> requestTransformations = proxySimConfig.asList(SimulatorProperties.simProxyRequestTransformations)
        requestTransformations.add('gov.nist.toolkit.fhir.simulators.proxy.transforms.NullEndpointTransform')
        proxySimConfig.setProperty(SimulatorProperties.simProxyRequestTransformations, requestTransformations)

        List<String> responseTransformations = proxySimConfig.asList(SimulatorProperties.simProxyResponseTransformations)
        //responseTransformations.add('gov.nist.toolkit.simProxy.server.transforms.MhdSubmissionTransform')
        proxySimConfig.setProperty(SimulatorProperties.simProxyResponseTransformations, responseTransformations)
        updatedProxySimConfig = spi.update(proxySimConfig)

        rrSimConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        spi.update(rrSimConfig)
    }

//    def cleanupSpec() {  // one time shutdown when everything is done
//        server.stop()
//        ListenerFactory.terminateAll()
//    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
//        api.createTestSession(testSession)
//        if (!api.simulatorExists(simId)) {
//            println "Creating sim ${simId}"
//            api.createSimulator(ActorType.REGISTRY, simId)
//        }
//
//        if (!api.simulatorExists(mhdSimId)) {
//            println "Creating sim ${mhdSimId}"
//            api.createSimulator(ActorType.SIM_PROXY, mhdSimId)
//        }
    }

    def 'send XDR through simproxy'() {
        when:
        SiteSpec siteSpec = new SiteSpec(simProxyName)
        TestInstance testInstance = new TestInstance('12360')
        List<String> sections = ['submit']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");

        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)
        SimDb simDb = new SimDb(simProxyId, ActorType.REPOSITORY, TransactionType.PROVIDE_AND_REGISTER, true)

        then:
        testOverviewDTO.sections.get('submit').pass
        simDb.getRequestHeaderFile().exists()
        simDb.getRequestBodyFile().exists()
        simDb.getResponseHdrFile().exists()
        simDb.getResponseBodyFile().exists()
    }

}
