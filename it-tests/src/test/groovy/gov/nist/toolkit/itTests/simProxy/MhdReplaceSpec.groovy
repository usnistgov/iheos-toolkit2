package gov.nist.toolkit.itTests.simProxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.AssertionResult
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.server.orchestration.FhirSupportOrchestrationBuilder
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import spock.lang.Shared

class MhdReplaceSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared String patientId2 = 'BR15^^^&1.2.360&ISO'
    @Shared String envName = 'test'
    @Shared String testSession = 'bill';
    @Shared String mhdId = "mhd"
    @Shared String mhdName = "${testSession}__${mhdId}"
    @Shared SimId mhdSimId = SimIdFactory.simIdBuilder(mhdName)
    @Shared SimConfig mhdSimConfig
    @Shared Map<String, SimConfig> simGroup = [:]

    def setupSpec() {   // one time setup done when class launched
        startGrizzlyWithFhir('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        new BuildCollections().init(null)

    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true, new TestSession(testSession))}"

        api.createTestSession(testSession)

        spi.delete(ToolkitFactory.newSimId(mhdId, testSession, ActorType.MHD_DOC_RECIPIENT.name, envName, true))

        Installation.instance().defaultEnvironmentName()

        mhdSimConfig = spi.create(
                mhdId,
                testSession,
                SimulatorActorType.MHD_DOC_RECIPIENT,
                envName
        )

        mhdSimConfig.asList(SimulatorProperties.simulatorGroup).each { String simIdString ->
            SimId theSimId = SimIdFactory.simIdBuilder(simIdString)
            SimConfig config = spi.get(spi.get(theSimId.testSession.value, theSimId.id))
            simGroup[simIdString] = config
        }

        SimConfig rrConfig = simGroup['bill__mhd_regrep']
        rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        spi.update(rrConfig)

        FhirSupportOrchestrationResponse response = new FhirSupportOrchestrationBuilder(api, session, new TestSession(testSession), false).buildTestEnvironment()
        assert !response.error
    }

    def 'submit and replace'() {
        when: 'submit pdb with known entryUUID urn:uuid:1e404af3-077f-4bee-b7a6-a9be97e1ce34'
        def params = [ :]
        TestInstance testInstance = new TestInstance('MhdReplace') // Note: bill__mhd site name is hard-coded in singledocsubmit.xml

        List<Result> results = api.runTest(testSession, mhdName, testInstance, ['submit'], params, true)

        then:
        results.size() == 1
        results.get(0).passed()
        results[0].assertions.getAssertionsThatContains('REF_DR0').size() == 1

        // Registry maintained specified entryUUID
        results[0].assertions.getAssertionsThatContains('REF_DR0').each { AssertionResult res ->
            assert res.assertion.contains('1e404af3-077f-4bee-b7a6-a9be97e1ce34')
        }

        when:  'submit replacement'
        List<Result> results2 = api.runTest(testSession, mhdName, testInstance, ['replace'], params, true)

        then:
        results2.size() == 1
        results2.get(0).passed()

    }
}
