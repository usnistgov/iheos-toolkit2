package gov.nist.toolkit.itTests.simProxy

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.client.IGenericClient
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.installation.ResourceCache
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.testkitutilities.TestDefinition
import gov.nist.toolkit.testkitutilities.TestKit
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.instance.model.api.IBaseResource
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
        List<String> requestTransformations = proxySimConfig.asList(SimulatorProperties.simProxyRequestTransformations)
        requestTransformations << 'gov.nist.toolkit.simulators.proxy.transforms.MhdToXdsEndpointTransform'
        requestTransformations << 'gov.nist.toolkit.simulators.proxy.transforms.MhdToPnrContentTransform'
        proxySimConfig.setProperty(SimulatorProperties.simProxyRequestTransformations, requestTransformations)

        List<String> responseTransformations = proxySimConfig.asList(SimulatorProperties.simProxyResponseTransformations)
        responseTransformations << 'gov.nist.toolkit.simulators.proxy.transforms.RegistryResponseToOperationOutcomeTransform'
        proxySimConfig.setProperty(SimulatorProperties.simProxyResponseTransformations, responseTransformations)

        updatedProxySimConfig = spi.update(proxySimConfig)

        rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        rrConfig.setProperty(SimulatorProperties.VALIDATE_CODES, false);
        spi.update(rrConfig)

    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }

    def 'send provide document bundle through simproxy'() {
        when:
        def sections = ['submit']
        def params = [ :]
        List<Result> results = api.runTest(testSession, simProxyName, testInstance, sections, params, true)

        then:
        results.size() == 1
        results.get(0).passed()
    }

    // HAPI is broken - generates GET instead of POST for transaction
//    def 'send provide document bundle from hapi client' () {
//        setup:
//        FhirContext ctx = ResourceCache.ctx
//        TestKit testKit = new TestKit(Installation.instance().internalTestkitFile())
//        TestDefinition testDef = testKit.getTestDef('MhdSubmit')
//        File testDir = testDef.testDir
//        File bundleFile = new File(new File(testDir, 'submit'), 'singledocsubmit.xml')
//        assert bundleFile.exists()
//        Bundle bundle = ctx.newXmlParser().parseResource(new FileReader(bundleFile))
//        String serverBase = 'http://localhost:7777/sim/bill__simproxy'
//
//        when:
//        IGenericClient client = ctx.newRestfulGenericClient(serverBase)
//        client.transaction().
//        def response = client.transaction().withBundle(bundle).execute()
//
//        then:
//        response
//        response.size() == 2
//
//    }
}
