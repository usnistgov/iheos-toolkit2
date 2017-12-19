package gov.nist.toolkit.itTests.xc

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
import gov.nist.toolkit.fhir.simulators.support.od.TransactionUtil
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 *  Based on OdRgConsumerSpec
 */
class RgRegOddsConsumerSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SRG13^^^&1.2.460&ISO'
    @Shared String id = 'odrg'
//    SimId simId = new SimId(reg)
    @Shared String testSession = 'billtest'
    @Shared String siteName = testSession + '__' + id
    @Shared SimConfig rgConfig = null
    @Shared Session tkSession

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        tkSession = UnitTestEnvironmentManager.setupLocalToolkit()

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        spi.delete(id, testSession)

        rgConfig = spi.create(
                id,
                testSession,
                SimulatorActorType.ON_DEMAND_RESPONDING_GATEWAY,
                'test')

//        spi.delete('od', testSession)
//
//        oddsConfig = spi.create(
//                'od',
//                testSession,
//                SimulatorActorType.ONDEMAND_DOCUMENT_SOURCE,
//                'test')
        rgConfig.setProperty(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS, false)
        rgConfig.setProperty(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT, "15806")
        rgConfig.setProperty(SimulatorProperties.oddePatientId, patientId)

//        // If Persistence Option: this is required: SimulatorProperties.oddsRepositorySite
//        List<String> regSites = new ArrayList<String>()
//        regSites.add(rgConfig.getFullId())
//        oddsConfig.setProperty(SimulatorProperties.oddsRegistrySite, regSites)
//        spi.update(oddsConfig)
//
//        rgConfig.setProperty(SimulatorProperties.retrieveEndpoint, oddsConfig.asString(SimulatorProperties.retrieveEndpoint) )
//        rgConfig.setProperty(SimulatorProperties.retrieveTlsEndpoint, oddsConfig.asString(SimulatorProperties.retrieveTlsEndpoint) )
//        spi.update(rgConfig)

    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        spi.delete('rg', testSession)
//        spi.delete('od', testSession)
        spi.delete(id, testSession)
        server.stop()
        ListenerFactory.terminateAll()
    }

    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid transaction to Registry simulator'() {
        when:
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
        paramsRegOdde.put('$repuid$', rgConfig.asString(SimulatorProperties.repositoryUniqueId))

        then:
        Map<String,String> rs = TransactionUtil.registerWithLocalizedTrackingInODDS(tkSession
                , rgConfig.getUser()
                , new TestInstance("15806")
                , new SiteSpec(rgConfig.getFullId(), ActorType.REGISTRY, null)
                , new SimId(rgConfig.getFullId())
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
