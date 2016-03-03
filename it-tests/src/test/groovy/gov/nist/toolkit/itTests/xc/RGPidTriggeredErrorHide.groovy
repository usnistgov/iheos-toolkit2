package gov.nist.toolkit.itTests.xc

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.actortransaction.client.TransactionType
import gov.nist.toolkit.configDatatypes.client.PatientError
import gov.nist.toolkit.configDatatypes.client.PatientErrorList
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServices.ToolkitFactory
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.SimId
import spock.lang.Shared
/**
 *
 */
class RGPidTriggeredErrorHide extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    BasicSimParameters RGParams = new BasicSimParameters();
    String patientId = 'BR14^^^&1.2.360&ISO'
    String testSession = 'bill'
    @Shared  apiEnvironment = 'test'
    @Shared  spiEnvironment = 'test'
    TestInstance testId
    List<String> sections
    Map<String, String> qparams
    boolean stopOnFirstError = true
    List<Result> results
    String RGSiteName = 'bill__rg1'


    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def setup() {  // run before each test method
        RGParams.id = 'rg1'
        RGParams.user = testSession
        RGParams.actorType = SimulatorActorType.RESPONDING_GATEWAY
        RGParams.environmentName = spiEnvironment
    }

    def 'test pid triggered error'() {
        when:
        println 'STEP - DELETE RESPONDING GATEWAY SIM'
        spi.delete(RGParams)

        and:
        println 'STEP - CREATE RESPONDING GATEWAY SIM'
        SimId RGSimId = spi.create(RGParams)

        then: 'verify sim built'
        RGSimId.getId() == RGParams.id

        when: 'force error on this patient id'
        SimConfig RGConfig = spi.get(RGSimId)

        PatientErrorMap errorMap = new PatientErrorMap()
        PatientError patientError = new PatientError()
        patientError.patientId = PidBuilder.createPid(patientId)
        patientError.errorCode = 'XDSMyError'
        PatientErrorList patientErrorList = new PatientErrorList()
        patientErrorList.add(patientError)
        errorMap.put(TransactionType.name, patientErrorList)

        RGConfig.setPatientErrorMap(errorMap)
        RGConfig = spi.update(RGConfig)
        println 'Updated rg config\n' + RGConfig.describe()

        and: 'create local site so test engine can reference it'
        SimConfig RGConfig2 = spi.get(RGSimId)
        SimulatorConfig rgSimConfig = ToolkitFactory.asSimulatorConfig(RGConfig2)
        println "local simconfig"
        println 'local rg site\n' + rgSimConfig.toString()
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig)

        and: 'cross community query to RG'
        testId = new TestInstance("FindDocuments")
        sections = ['XCA']
        qparams = new HashMap<>()
        qparams.put('$patient_id$', patientId)

        and: 'Run xcq test'
        println 'STEP - CROSS COMMUNITY QUERY'
        results = api.runTest(testSession, RGSiteName, testId, sections, qparams, stopOnFirstError)

        then:  'verify query failed'
        results.size() == 1
        results.get(0).passed()

    }
}
