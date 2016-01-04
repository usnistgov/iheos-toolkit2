package gov.nist.toolkit.itTests.xds
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import spock.lang.Shared
/**
 * Runs all Registry tests.
 * To run:
 *    Start toolkit from IntelliJ.
 *    Open Simulation Manager
 *    Select test session named mike (create it if it doesn't exist)
 *    Create a Registry simulator named reg - the full id will be mike__reg
 *    Come back to this file in IntelliJ and click right on the class name and select Run RegistrySelfTestIT
 *    All the self tests will run
 */
class RegistrySelfTestSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    String patientId = 'BR14^^^&1.2.360&ISO'
    String patientId2 = 'BR15^^^&1.2.360&ISO'
    String reg = 'mike__reg'
    SimId simId = new SimId(reg)
    @Shared String testSession = 'mike';

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        spi.delete('reg', testSession)

        spi.create(
                'reg',
                testSession,
                SimulatorActorType.REGISTRY,
                'test')
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
            api.createSimulator(ActorType.REGISTRY, simId)
        }
    }

    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid transaction to Registry simulator'() {
        when:
        String siteName = 'mike__reg'
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

    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit second Pid transaction to Registry simulator'() {
        when:
        String siteName = 'mike__reg'
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId2)
        boolean stopOnFirstError = true

        and: 'Run pid transaction test'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }


//    def 'Run 11990'() {
//        when:
//        String testSession = null;  // use default
//        String siteName = 'mike__reg'
//        String testId = "11990"
//        List<String> sections = new ArrayList<>()
//        Map<String, String> params = new HashMap<>()
//        params.put('$patientid$', patientId)
//        boolean stopOnFirstError = true
//
//        and: 'Run Register test'
//        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
//
//        then:
//        true
//        results.size() == 1
//        results.getRetrievedDocumentsModel(0).passed()
//    }

//    def 'Run test under debug'() {
//        when:
//        String testSession = null;  // use default
//        String siteName = 'mike__reg'
//        String testId = "12002"
//        List<String> sections = new ArrayList<>()
//        Map<String, String> params = new HashMap<>()
//        params.put('$patientid$', patientId)
//        boolean stopOnFirstError = true
//
//        and: 'Run Register test'
//        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
//
//        then:
//        true
//        results.size() == 1
//        results.getRetrievedDocumentsModel(0).passed()
//    }

    def 'Run all Register tests'() {
        when:
        String siteName = 'mike__reg'
        TestInstance testId = new TestInstance("tc:R.b")
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

    def 'Run SQ initialization'() {
        when:
        String siteName = 'mike__reg'
        TestInstance testId = new TestInstance("tc:Initialize_for_Stored_Query")
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId2)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    def 'Run SQ tests'() {
        when:
        String siteName = 'mike__reg'
        TestInstance testId = new TestInstance("tc:SQ.b")
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId2)   // not used
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

//    def 'Run all PnR tests'() {
//        when:
//        String testSession = null;  // use default
//        String siteName = 'mike__regrep'
//        String testId = "tc:PR.b"
//        List<String> sections = new ArrayList<>()
//        Map<String, String> params = new HashMap<>()
//        params.put('$patientid$', patientId)
//        boolean stopOnFirstError = true
//
//        and: 'Run'
//        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
//
//        then:
//        true
//        results.size() == 1
//        results.getRetrievedDocumentsModel(0).passed()
//    }

}
