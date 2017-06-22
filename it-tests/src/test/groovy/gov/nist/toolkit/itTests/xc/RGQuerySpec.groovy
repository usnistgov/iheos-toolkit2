package gov.nist.toolkit.itTests.xc

import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.toolkitApi.BasicSimParameters
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServices.ToolkitFactory
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.SimId
import spock.lang.Shared

/**
 *
 */
class RGQuerySpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared  BasicSimParameters RGParams = new BasicSimParameters();
    @Shared  BasicSimParameters IGParams = new BasicSimParameters();
    @Shared  String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared  String testSession = 'bill'
    @Shared  apiEnvironment = 'test'
    @Shared  spiEnvironment = 'test'
    @Shared  TestInstance testId
    @Shared  List<String> sections
    @Shared  Map<String, String> qparams
    @Shared  boolean stopOnFirstError = true
    @Shared  List<Result> results
    @Shared  String RGSiteName = 'bill__rg1'
    @Shared  SimId RGSimId
    @Shared  SimId IGSimId

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        if (RGSimId)
            spi.delete(RGSimId)
        if (IGSimId)
            spi.delete(IGSimId)
    }


    def setup() {  // run before each test method
        RGParams.id = 'rg1'
        RGParams.user = testSession
        RGParams.actorType = SimulatorActorType.RESPONDING_GATEWAY
        RGParams.environmentName = spiEnvironment
    }

    def 'GetDocuments with homeComunityId missing'() {
        when:
//        System.gc()
        println 'STEP - DELETE RESPONDING GATEWAY SIM'
        spi.delete(RGParams)

        and:
        println 'STEP - CREATE RESPONDING GATEWAY SIM'
        RGSimId = spi.create(RGParams)
        SimConfig RGConfig = spi.get(RGSimId)

        then: 'verify sim built'
        RGSimId.getId() == RGParams.id

        when: 'create local site so test engine can reference it'
        SimulatorConfig rgSimConfig = ToolkitFactory.asSimulatorConfig(RGConfig)
        println "local simconfig"
        println 'local rg site\n' + rgSimConfig.toString()
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig)

        and: 'issue XC GetDocuments - homeCommunityId is missing from parameters'
        testId = new TestInstance('rg-bad-home')
        qparams = new HashMap<String, String>()
        results = api.runTest(testSession, RGSiteName, testId, sections, qparams, stopOnFirstError)

        then:  'verify query return errors (built into TestPlan)'
        results.size() == 1
        results.get(0).passed()

    }

}
