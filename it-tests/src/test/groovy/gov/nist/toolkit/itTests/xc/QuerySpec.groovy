package gov.nist.toolkit.itTests.xc
import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.registrymetadata.client.Uid
import gov.nist.toolkit.registrymetadata.client.Uids
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentManager
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel
import gov.nist.toolkit.registrysupport.MetadataSupport
import gov.nist.toolkit.results.client.CodesConfiguration
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.toolkitApi.BasicSimParameters
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServices.ToolkitFactory
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType
import spock.lang.Shared
/**
 * Build RG, initialize it with a single submission, query and retreive the submission.
 *
 * This test uses two copies of toolkit.  One copy is launched via Grizzly and offers the
 * simulator services.  It is references through the variable spi.
 *
 * The second copy is referenced as the testclient and is run this is thread.  It is initialized
 * and referenced through the variable api.
 */
class QuerySpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    BasicSimParameters RGParams = new BasicSimParameters();
    BasicSimParameters IGParams = new BasicSimParameters();
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
//        RGSiteName = RGParams.user + '__' + RGParams.id

        IGParams.id = 'ig'
        IGParams.user = testSession
        IGParams.actorType = SimulatorActorType.INITIATING_GATEWAY
        IGParams.environmentName = spiEnvironment
    }

    def 'Test Responding Gateway' () {
        when:
        println 'STEP - DELETE RESPONDING GATEWAY SIM'
        spi.delete(RGParams)

        and:
        println 'STEP - CREATE RESPONDING GATEWAY SIM'
        SimId RGSimId = spi.create(RGParams)

        then: 'verify sim built'
        RGSimId.getId() == RGParams.id

        when: 'disable checking of Patient Identity Feed'
        SimConfig RGConfig = spi.get(RGSimId)
        RGConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        RGConfig = spi.update(RGConfig)
        println 'Updated rg config\n' + RGConfig.describe()

        and: 'create local site so test engine can reference it'
        SimulatorConfig rgSimConfig = ToolkitFactory.asSimulatorConfig(RGConfig)
        println "local simconfig"
        println 'local rg site\n' + rgSimConfig.toString()
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig)

        and: 'Submit one Document to Rep/Reg behind RG'
//        String testSession = testSession;  // use default
        testId = new TestInstance("12318")
        sections = null
        qparams = new HashMap<>()
        qparams.put('$patientid$', patientId)

        and: 'execute submit to reg/rep'
        println 'STEP - LOAD REGISTRY'
        results = api.runTest(testSession, RGSiteName, testId, sections, qparams, stopOnFirstError)
        println 'registry load complete'

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

        when: 'retrieve'
        println 'STEP - SEND RETRIEVES TO IG'
        RetrievedDocumentsModel retModels = RetrievedDocumentManager.getRetrievedDocumentsModel(metadataCollections.get(0))
        RetrievedDocumentModel retModel = retModels.values().first()
        println ("ret model is " + retModel)
        Uid uid = new Uid(retModels.keySet().first())
        uid.home = retModel.home
        println "uid is " + uid
        Uids uids = new Uids()
        uids.add(uid)
        results = api.retrieveDocuments(site, uids)

        then:
        results.size() == 1
        results.get(0).passed()
    }

}
