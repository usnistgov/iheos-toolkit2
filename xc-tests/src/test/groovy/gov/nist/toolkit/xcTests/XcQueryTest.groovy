package gov.nist.toolkit.xcTests

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentManager
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel
import gov.nist.toolkit.registrysupport.MetadataSupport
import gov.nist.toolkit.results.client.CodesConfiguration
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServices.ToolkitFactory
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType
import spock.lang.Shared
import spock.lang.Specification
/**
 * Build RG, initialize it with a single submission, query and retreive the submission.
 *
 * This test uses two copies of toolkit.  One copy is launched via Grizzly and offers the
 * simulator services.  It is references through the variable spi.
 *
 * The second copy is referenced as the testclient and is run this is thread.  It is initialized
 * and referenced through the variable api.
 */
class XcQueryTest extends Specification {
    @Shared ToolkitApi api;
    @Shared def port = '8888'
    @Shared def externalPort = '8888'
    @Shared String urlRoot  // = String.format("http://localhost:%s/xdstools2", port)
    @Shared SimulatorBuilder spi  // = new SimulatorBuilder(urlRoot)
    @Shared server
    BasicSimParameters RGParams = new BasicSimParameters();
    BasicSimParameters IGParams = new BasicSimParameters();
    String patientId = 'BR14^^^&1.2.360&ISO'
    String testSession = 'mike'
    @Shared  apiEnvironment = 'test'
    @Shared  spiEnvironment = 'test'
    TestInstance testId
    List<String> sections
    Map<String, String> qparams
    boolean stopOnFirstError = true
    List<Result> results
    String RGSiteName = 'mike__rg1'
    String IGSiteName = 'mike__ig'

    // test with (or not) with toolkit launched in Grizzly
    // if not then toolkit must be launched externally and the above port # changed to match
    // You might want to do this to debug a particular simulator, the external toolkit
    // provides the full user interface
    // BTW, for now it is also necessary to update toolkit.properties
    // in this module to the port used by the external toolkit.  Otherwise
    // it gets over written in a wierd way I cannot yet understand.
    @Shared boolean useInternalToolkit = false


    def setupSpec() {   // one time setup done when class launched
        TestSession.setupToolkit()
        api = ToolkitApi.forServiceUse()

        if (useInternalToolkit) {
            server = new GrizzlyController()
            server.start(port);
            server.withToolkit()
        } else {
            port = externalPort
            Installation.installation().overrideToolkitPort(port)  // ignore toolkit.properties
        }
        urlRoot = String.format("http://localhost:%s/xdstools2", port)
        spi = new SimulatorBuilder(urlRoot)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        if (useInternalToolkit)
            server.stop()
    }

    def setup() {  // run before each test method
        RGParams.id = 'rg1'
        RGParams.user = testSession
        RGParams.actorType = SimulatorActorType.RESPONDING_GATEWAY
        RGParams.environmentName = spiEnvironment

        IGParams.id = 'ig'
        IGParams.user = testSession
        IGParams.actorType = SimulatorActorType.INITIATING_GATEWAY
        IGParams.environmentName = spiEnvironment
    }

    def 'Test Responding Gateway' () {
        when:
        println 'STEP - DELETE RESPONDING GATEWAY SIM'
        spi.delete(RGParams.id, RGParams.user)

        and:
        println 'STEP - CREATE RESPONDING GATEWAY SIM'
        SimId RGSimId = spi.create(
                RGParams.id,
                RGParams.user,
                RGParams.actorType,
                RGParams.environmentName
        )

        then: 'verify sim built'
        RGSimId.getId() == RGParams.id

        when: 'disable checking of Patient Identity Feed'
        SimConfig RGConfig = spi.get(RGSimId)
        RGConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        RGConfig = spi.update(RGConfig)
        println RGConfig.describe()

        and: 'create local site so test engine can reference it'
        SimulatorConfig rgSimConfig = ToolkitFactory.asSimulatorConfig(RGConfig)
        println "local simconfig"
        println rgSimConfig.toString()
        SimCache.getSimManagerForSession(Installation.defaultSessionName(), true).addSimConfig(rgSimConfig)

        and: 'Submit one Document to Rep/Reg behind RG'
//        String testSession = testSession;  // use default
        testId = new TestInstance("12318")   //11966
        sections = null
        qparams = new HashMap<>()
        qparams.put('$patientid$', patientId)

        and: 'Run Register test'
        println 'STEP - LOAD REGISTRY'
        results = api.runTest(testSession, RGSiteName, testId, sections, qparams, stopOnFirstError)

        then:  'verify register worked'
        results.size() == 1
        results.get(0).passed()

        when: 'cross community query to RG to verify test data'
        testId = new TestInstance("12310")
        sections = null
        qparams = new HashMap<>()
        qparams.put('$patientid$', patientId)

        and: 'Run xcq test'
        println 'STEP - CROSS COMMUNITY QUERY'
        results = api.runTest(testSession, RGSiteName, testId, sections, qparams, stopOnFirstError)

        then:  'verify query worked'
        results.size() == 1
        results.get(0).passed()

        when:
        println 'STEP - DELETE INITIATING GATEWAY SIM'
        spi.delete(IGParams.id, IGParams.user)

        and:
        println 'STEP - CREATE INITIATING GATEWAY SIM'
        SimId IGSimId = spi.create(
                IGParams.id,
                IGParams.user,
                IGParams.actorType,
                IGParams.environmentName
        )

        then: 'verify sim built'
        IGSimId.getId() == IGParams.id

        when: 'link ig to rg'
        SimConfig IGConfig = spi.get(IGSimId)
        IGConfig.setProperty(SimulatorProperties.respondingGateways, [ RGSimId.getFullId() ])
        SimConfig updatedIGConfig = spi.update(IGConfig)
        println updatedIGConfig.describe()

        then:
        updatedIGConfig.asList(SimulatorProperties.respondingGateways) == [ RGSimId.getFullId() ]

        when: 'send FindDocuments stored query to IG'
        println 'STEP - SEND FIND DOCUMENTS TO IG'
        SimulatorConfig igSimConfig = ToolkitFactory.asSimulatorConfig(updatedIGConfig)
        SimCache.getSimManagerForSession(Installation.defaultSessionName(), true).addSimConfig(igSimConfig)
        println 'Site names ' + api.getSiteNames(true)
        def site = new SiteSpec(String.format('%s__%s', IGParams.user, IGParams.id), ActorType.INITIATING_GATEWAY, null)
        Map<String, List<String>> selectedCodes = new HashMap<>()
        selectedCodes.put(CodesConfiguration.DocumentEntryStatus, [MetadataSupport.statusType_approved])
        selectedCodes.put(CodesConfiguration.ReturnsType, [QueryReturnType.LEAFCLASS.getReturnTypeString()])
        results = api.findDocuments(site, patientId, selectedCodes)
        println results

        then:
        results.size() == 1
        results.get(0).passed()

        when: 'getRetrievedDocumentsModel returned metadata'
        List<MetadataCollection> metadataCollections = results.get(0).getMetadataContent()

        then: 'verify query returned 2 DocEntries'
        metadataCollections.size() == 1
        metadataCollections.get(0).docEntries.size() == 2

        when: ''
        RetrievedDocumentsModel retModels = RetrievedDocumentManager.getRetrievedDocumentsModel(metadataCollections.get(0))

        then:
        true
    }

}
