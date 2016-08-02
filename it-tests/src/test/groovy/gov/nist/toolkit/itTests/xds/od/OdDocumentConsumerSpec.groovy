package gov.nist.toolkit.itTests.xds.od

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Document Consumer Actor tests
 */
class OdDocumentConsumerSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    String patientId = 'SKB1^^^&1.2.960&ISO'
    @Shared String reg = 'sunil__rr'
    SimId simId = new SimId(reg)
    @Shared String testSession = 'sunil';

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        spi.delete('rr', testSession)

        spi.create(
                'rr',
                testSession,
                SimulatorActorType.REPOSITORY_REGISTRY,
                'test')

        spi.delete('odds', testSession)


        SimConfig simConfig = spi.create(
                'odds',
                testSession,
                SimulatorActorType.ONDEMAND_DOCUMENT_SOURCE,
                'test')

        println "*** describe odds sim: ${simConfig.describe()}"

        // Persistence should be ON by default in the simulator config

//        odds_sim.getConfig()

        //SimulatorConfigElement sce =  odds_sim.getConfigs().get(SimulatorProperties.oddsRepositorySite);

        //println "got Odds sce: ${sce.asList()}"
/*
        // Set the repository/reg site in the ODDS config
        for (int cx=0; cx<odds_sim.getConfigs().size(); cx++) {
            if (sce.equals(odds_sim.getConfig(cx))) {
                for (String site : sce.asList()) {
                    if (reg.equals(site)) {
                        List<String> value = new ArrayList<String>()
                        value.add(reg)
                        sce.setValue(value)
                    }
                }
                odds_sim.getConfigs().set(cx,sce)

            }
        }
*/
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.installation().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
        api.createTestSession(testSession)
        if (!api.simulatorExists(simId)) {
            println "Creating sim ${simId}"
            api.createSimulator(ActorType.REPOSITORY_REGISTRY, simId)
        }
    }

    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid transaction to Registry simulator'() {
        when:
        String siteName = 'sunil__rr'
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

    /**
     * Initialize the ODDS state
     * @return
     */
    def 'Run Register test'() {
        when:
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15812")
        List<String> sections = new ArrayList<>()
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

    /**
     * DocCons: Make sure OD registration was successful
     * @return
     */
    def 'Run query test'() {
        when:
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15812")
        List<String> sections = ["Query_OD"]
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



    /**
     * DocCons: retrieve
     * @return
     */
    def 'Run retrieve test'() {
        when:
        String siteName = 'sunil__odds'
        TestInstance testId = new TestInstance("15812")
        List<String> sections = ["Retrieve_OD"]
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

}
