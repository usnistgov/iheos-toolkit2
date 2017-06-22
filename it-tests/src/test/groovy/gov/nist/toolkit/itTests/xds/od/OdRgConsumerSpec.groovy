package gov.nist.toolkit.itTests.xds.od

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
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
 * Test Responding gateway with On-Demand Document Source
 */
class OdRgConsumerSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SRG13^^^&1.2.460&ISO'
    String reg = 'sunil__rg'
    SimId simId = new SimId(reg)
    @Shared String testSession = 'sunil'
    @Shared SimConfig rgConfig = null
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

        spi.delete('rg', testSession)

        rgConfig = spi.create(
                'rg',
                testSession,
                SimulatorActorType.RESPONDING_GATEWAY,
                'test')

        spi.delete('od', testSession)

        oddsConfig = spi.create(
                'od',
                testSession,
                SimulatorActorType.ONDEMAND_DOCUMENT_SOURCE,
                'test')
        oddsConfig.setProperty(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS, false)
        oddsConfig.setProperty(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT, "15806")
        oddsConfig.setProperty(SimulatorProperties.oddePatientId, patientId)
        // If Persistence Option: this is required: SimulatorProperties.oddsRepositorySite
        List<String> regSites = new ArrayList<String>()
        regSites.add(rgConfig.getFullId())
        oddsConfig.setProperty(SimulatorProperties.oddsRegistrySite, regSites)
        spi.update(oddsConfig)

        rgConfig.setProperty(SimulatorProperties.retrieveEndpoint, oddsConfig.asString(SimulatorProperties.retrieveEndpoint) )
        rgConfig.setProperty(SimulatorProperties.retrieveTlsEndpoint, oddsConfig.asString(SimulatorProperties.retrieveTlsEndpoint) )
        spi.update(rgConfig)

    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        System.gc()
        spi.delete('rg', testSession)
        spi.delete('od', testSession)
        server.stop()
        ListenerFactory.terminateAll()
    }


    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid transaction to Registry simulator'() {
        when:
        String siteName = 'sunil__rg'
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

    def 'Initialize ODDS with an ODDE'() {
        when:
        // For testing purposes, manual initialization of the ODDS with the ODDE is required
        Map<String, String> paramsRegOdde = new HashMap<>()
        paramsRegOdde.put('$patientid$', patientId)
        paramsRegOdde.put('$repuid$', oddsConfig.asString(SimulatorProperties.repositoryUniqueId))

        then:
        Map<String,String> rs = TransactionUtil.registerWithLocalizedTrackingInODDS(tkSession
                , oddsConfig.getUser()
                , new TestInstance("15806")
                , new SiteSpec(rgConfig.getFullId(), ActorType.REGISTRY, null)
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
    def 'Retrieve from the ODDS without Persistence Option'() {
        when:
        String siteName = 'sunil__rg'
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
        boolean noNewRepository = true
        for (StepResult sr : results.get(0).stepResults) {
            for (Document d : sr.documents) {
                System.out.println("d.newUid=" + d.newUid)
                System.out.println("d.newRepositoryUniqueId="+ d.newRepositoryUniqueId)
                if (noNewRepository)
                    noNewRepository = (d.newRepositoryUniqueId==null)
                if (!duplicates)
                    duplicates = uids.containsKey(d.newUid) // Make sure the uid is unique
                uids.put(d.newUid,null)
                documentsCt++
            }
        }

        then:
        results.size() == 1

        results.get(0).passed()

        !duplicates

        noNewRepository

        documentsCt==2 // There are two unique documents in the content bundle for one OD



    }

}
