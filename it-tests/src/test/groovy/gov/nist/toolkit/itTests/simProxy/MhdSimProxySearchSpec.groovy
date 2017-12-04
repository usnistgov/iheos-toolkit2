package gov.nist.toolkit.itTests.simProxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.fhir.utility.FhirClient
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import org.apache.http.annotation.Obsolete
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.instance.model.api.IBaseResource
import spock.lang.Shared

class MhdSimProxySearchSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot // = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
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
    @Shared String baseAddress //= "${urlRoot}/fsim/${mhdName}/fhir"

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')
        urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
        baseAddress = "${urlRoot}/fsim/${mhdName}/fhir"
        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        def build = true
        if (build) {
            new BuildCollections().init(null)

            // Build MHD Document Recipient

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
                SimConfig config = spi.get(spi.get(theSimId.user, theSimId.id))
                simGroup[simIdString] = config
            }

            SimConfig rrConfig = simGroup['bill__mhd_regrep']
            rrConfig.setProperty(SimulatorProperties.VALIDATE_CODES, false)
            rrConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
            spi.update(rrConfig)

            // load MHD Doc Rec with single one-doc submission

            def sections = ['pdb']
            def params = [:]
            List<Result> results = api.runTest(testSession, mhdName, testInstance, sections, params, true)

            assert results.size() == 1
            assert results.get(0).passed()
            assert results[0].assertions.getAssertionsThatContains('Ref =').size() == 2
        }
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }

    // this fails because when ProvideDocumentBundleTransaction tries to verify returned fullUrls, to create
    // the resource to return, the Recipient must query the patient provider which is not running.
    // To run this test we need to recexamine the test setup - not deep enough
    @Obsolete
    def 'Find Document References search through simproxy'() {
        when:
        def config = spi.get(spi.get(testSession, mhdId))
        def baseAddr = config.asString(SimulatorProperties.fhirEndpoint)
        Map<String, IBaseResource> result = new FhirClient().search(baseAddr,
                'DocumentReference',
                ['patient.identifier=urn:oid:1.2.3.4.5.6|MRN',
                'status=current'])

        then:
        !result.isEmpty()
        result.values()[0] instanceof DocumentReference
    }
}
