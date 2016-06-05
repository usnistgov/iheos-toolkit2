package gov.nist.toolkit.itTests.xds.od

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simulators.support.od.TransactionUtil
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Test the Register transaction
 */
class OdConsumerPersistenceSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SR15^^^&1.2.460&ISO'
    String reg = 'sunil2__rr3'
    SimId simId = new SimId(reg)
    @Shared String testSession = 'sunil2';
    @Shared SimConfig rrConfig = null
    @Shared SimConfig oddsConfig = null;
    @Shared Session tkSession

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        tkSession = UnitTestEnvironmentManager.setupLocalToolkit()

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        spi.delete('rr3', testSession)

        rrConfig = spi.create(
                'rr3',
                testSession,
                SimulatorActorType.REPOSITORY_REGISTRY,
                'test')

        spi.delete('odds3', testSession)

        oddsConfig = spi.create(
                'odds3',
                testSession,
                SimulatorActorType.ONDEMAND_DOCUMENT_SOURCE,
                'test')
        oddsConfig.setProperty(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS, true)
        oddsConfig.setProperty(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT, "15806")
        oddsConfig.setProperty(SimulatorProperties.oddePatientId, patientId)

        List<String> regSites = new ArrayList<String>()
        regSites.add(rrConfig.getFullId())
        oddsConfig.setProperty(SimulatorProperties.oddsRegistrySite, regSites)
        // If Persistence Option: this is required: SimulatorProperties.oddsRepositorySite
        List<String> repSites = new ArrayList<String>()
        repSites.add(rrConfig.getFullId())
        oddsConfig.setProperty(SimulatorProperties.oddsRepositorySite, repSites)

        spi.update(oddsConfig)

}

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }


    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid transaction to Registry simulator'() {
        when:
        String siteName = 'sunil2__rr3'
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run pid transaction test'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    def 'Register OD'() {
        when:
        // For testing purposes, manual initialization of the ODDS with the ODDE is required
        Map<String, String> paramsRegOdde = new HashMap<>()
        paramsRegOdde.put('$patientid$', patientId)
        paramsRegOdde.put('$repuid$', oddsConfig.asString(SimulatorProperties.repositoryUniqueId))

        then:
        Map<String,String> rs = TransactionUtil.registerWithLocalizedTrackingInODDS(tkSession
                , oddsConfig.getUser()
                , new TestInstance("15806")
                , new SiteSpec(rrConfig.getFullId(), ActorType.REGISTRY, null)
                , new SimId(oddsConfig.getFullId())
                , paramsRegOdde)

        for (String key : rs.keySet()) {
            System.out.println("*** regOdde key:" + key + ": " + rs.get(key))
        }

    }

    /**
     *
     * @return
     */
    def 'Run retrieve with Persistence Option'() {
        when:
        String siteName = 'sunil2__odds3'
        TestInstance testId = new TestInstance("15806")
        List<String> sections = ["Retrieve"]
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

}
