package gov.nist.toolkit.itTests.simProxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.AssertionResult
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
    @Shared String rrId = "${testSession}__${id}"
    @Shared SimId simId = new SimId(rrId)  // ultimate destination
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
                SimulatorActorType.XDS_on_FHIR_Recipient,
                envName
        )

        proxySimConfig.setProperty(SimulatorProperties.proxyForwardSite, rrId)

        updatedProxySimConfig = spi.update(proxySimConfig)

        rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        rrConfig.setProperty(SimulatorProperties.VALIDATE_CODES, false);
        spi.update(rrConfig)

    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }

    def 'send provide document bundle through simproxy'() {
        when:
        def sections = ['pdb']
        def params = [ :]
        List<Result> results = api.runTest(testSession, simProxyName, testInstance, sections, params, true)

        then:
        results.size() == 1
        results.get(0).passed()
        results[0].assertions.getAssertionsThatContains('Ref =').size() == 2

        when:  // parse ids
        def ids = results[0].assertions.assertions.findAll {
            it.assertion.contains('Ref =')
        }.collect { AssertionResult ar ->
            ar.assertion.substring('Ref = '.size()).trim()  // collect the References returned
        }.collect { it.split('=')[1].trim()}  // format is Builder: Ref = DocumentManifest/SubmissionSet_ID02

        then:  'type included'
        ids.size() == 2
        ids[1].startsWith('DocumentManifest')
        ids[0].startsWith('DocumentReference')

        // id's are UUIDs
        ids[0].split('/')[1].startsWith('urn:uuid:')
        ids[1].split('/')[1].startsWith('urn:uuid:')
    }
}
