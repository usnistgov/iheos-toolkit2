package gov.nist.toolkit.itTests.xds.od

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymetadata.client.Document
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.StepResult
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simulators.support.od.TransactionUtil
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Test the OD Document Consumer Retrieve
 */
class OdConsumerPersistenceSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SR15^^^&1.2.460&ISO'
    @Shared String testSession = 'sunil2'
    @Shared SimConfig rrConfig = null
    @Shared SimConfig oddsConfig = null
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
//        System.gc()
        spi.delete('rr3', testSession)
        spi.delete('odds3', testSession)
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

    def 'Run retrieve with Persistence Option'() {
        when:
        String siteName = 'sunil2__odds3'
        TestInstance testId = new TestInstance("15806")
        List<String> SECTIONS = ["Retrieve"]
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, SECTIONS, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }
    */

    /**
     *
     * @return
     */
    def 'Retrieve from the ODDS with Persistence Option'() {
        when:
        String siteName = 'sunil2__odds3'
        TestInstance testId = new TestInstance("15806")
        List<String> sections = ["Retrieve"]
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true
        Map<String,String> uids = new HashMap<>()

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
        uids.put(results.get(0).stepResults.get(0).getMetadata().docEntries.get(0).uniqueId,null)
        int documentsCt = 0;
        boolean duplicates = false
        List<Result> persistedRetrieveRs = new ArrayList<>()

        for (StepResult sr : results.get(0).stepResults) {
            for (Document d : sr.documents) {
                documentsCt++
                System.out.println("d.cacheURL=" + d.cacheURL)
                System.out.println("d.newUid=" + d.newUid)
                System.out.println("d.newRepositoryUniqueId="+ d.newRepositoryUniqueId)

                params.put('$repuid'+ documentsCt +'$', d.newRepositoryUniqueId)
                params.put('$od_snapshot_uid'+ documentsCt +'$', d.newUid)

                if (!duplicates)
                    duplicates = uids.containsKey(d.newUid) // Make sure the uid is unique
                uids.put(d.newUid,null)
            }
        }

        sections = ["Retrieve_Snapshot"]
        persistedRetrieveRs.add(api.runTest(testSession, rrConfig.getFullId(), testId, sections, params, stopOnFirstError).get(0))

        then:
        results.size() == 1
        results.get(0).passed()
        !duplicates
        documentsCt==2 // There are two unique documents in the content bundle for one OD

        for (Result rs : persistedRetrieveRs)
            rs.passed()

    }


}
