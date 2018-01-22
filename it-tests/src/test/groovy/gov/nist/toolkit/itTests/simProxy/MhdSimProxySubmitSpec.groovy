package gov.nist.toolkit.itTests.simProxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.AssertionResult
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.server.orchestration.FhirSupportOrchestrationBuilder
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import spock.lang.Shared
/**
 * Test SimProxy with MHD -> XDS transformation as front end to RegRepSpec simulator
 */
class MhdSimProxySubmitSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared String patientId2 = 'BR15^^^&1.2.360&ISO'
    @Shared String envName = 'test'
    @Shared String testSession = 'bill';
    @Shared String mhdId = "mhd"
    @Shared String mhdName = "${testSession}__${mhdId}"
    @Shared SimId mhdSimId = new SimId(mhdName)
    @Shared SimConfig mhdSimConfig
    @Shared TestInstance testInstance = new TestInstance('MhdSubmit')
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
        println "${api.getSiteNames(true)}"
    }

    def 'send provide document bundle through simproxy'() {
        setup:
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
            SimId theSimId = new SimId(simIdString)
            SimConfig config = spi.get(spi.get(theSimId.testSession, theSimId.id))
            simGroup[simIdString] = config
        }

        //println simGroup
        SimConfig rrConfig = simGroup['bill__mhd_regrep']
//        rrConfig.setProperty(SimulatorProperties.VALIDATE_CODES, false)
        rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        spi.update(rrConfig)

        when:
        FhirSupportOrchestrationResponse response = new FhirSupportOrchestrationBuilder(api, session, testSession, false).buildTestEnvironment()

        then:
        !response.error

        when:
        def sections = ['pdb']
        def params = [ :]
        List<Result> results = api.runTest(testSession, mhdName, testInstance, sections, params, true)

        and:
        SimDb simDb = new SimDb(mhdSimId)
        simDb.openMostRecentEvent(ActorType.MHD_DOC_RECIPIENT, TransactionType.PROV_DOC_BUNDLE)
        println simDb.getLogFile().text

        then:
        results.size() == 1
        results.get(0).passed()
        results[0].assertions.getAssertionsThatContains('REF_DR').size() == 1
        results[0].assertions.getAssertionsThatContains('REF_DM').size() == 1
    }

    def 'send provide document bundle through simproxy - bad reference to Patient'() {
        setup:
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
            SimId theSimId = new SimId(simIdString)
            SimConfig config = spi.get(spi.get(theSimId.testSession, theSimId.id))
            simGroup[simIdString] = config
        }

        //println simGroup
        SimConfig rrConfig = simGroup['bill__mhd_regrep']
        rrConfig.setProperty(SimulatorProperties.VALIDATE_CODES, false)
        rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        spi.update(rrConfig)

        when:
        def sections = ['pdb_external_patient']
        def params = [ :]
        List<Result> results = api.runTest(testSession, mhdName, testInstance, sections, params, true)

        and:
        SimDb simDb = new SimDb(mhdSimId)
        simDb.openMostRecentEvent(ActorType.MHD_DOC_RECIPIENT, TransactionType.PROV_DOC_BUNDLE)

        then:
        results.size() == 1
        !results.get(0).passed()
        results[0].assertions.assertions.find { AssertionResult ar ->
            ar.toString().contains('Patient/external1 cannot be resolved')
        }
    }

    def 'send provide document bundle through simproxy - missing sourcePatient'() {
        setup:
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
            SimId theSimId = new SimId(simIdString)
            SimConfig config = spi.get(spi.get(theSimId.testSession, theSimId.id))
            simGroup[simIdString] = config
        }

        //println simGroup
        SimConfig rrConfig = simGroup['bill__mhd_regrep']
        rrConfig.setProperty(SimulatorProperties.VALIDATE_CODES, false)
        rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        spi.update(rrConfig)

        when:
        def sections = ['pdb_missing_sourcePatient']
        def params = [ :]
        List<Result> results = api.runTest(testSession, mhdName, testInstance, sections, params, true)

        and:
        SimDb simDb = new SimDb(mhdSimId)
        simDb.openMostRecentEvent(ActorType.MHD_DOC_RECIPIENT, TransactionType.PROV_DOC_BUNDLE)

        then:
        results.size() == 1
        !results.get(0).passed()
        results[0].assertions.assertions.find { AssertionResult ar ->
            ar.toString().contains('#a2 cannot be resolved')
        }
    }

    def 'find transactions in simulator log'() {
        when:
        List<FhirSimulatorTransaction> trans = FhirSimulatorTransaction.getAll(mhdSimId, TransactionType.FHIR)

        then:
        trans.size() == 1
    }
}
