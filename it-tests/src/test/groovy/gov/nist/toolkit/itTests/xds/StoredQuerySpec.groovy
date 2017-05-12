package gov.nist.toolkit.itTests.xds
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared
/**
 * Runs all Registry tests.
 * To run:
 *    Start toolkit from IntelliJ.
 *    Open Simulation Manager
 *    Select test session named bill (create it if it doesn't exist)
 *    Create a Registry simulator named reg - the full id will be bill__reg
 *    Come back to this file in IntelliJ and click right on the class name and select Run RegistrySelfTestIT
 *    All the self tests will run
 */
class StoredQuerySpec extends ToolkitSpecification {

    // TODO there is an issue with this test. Every other time the test succeeds and every other time the two SQ tests
    // TODO - fail regardless of data used. Should delete then recreate the simulator bill_reg at the beginning.


    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    String patientId2 = 'BR15^^^&1.2.360&ISO'
    String reg = 'bill__reg'
    SimId simId = new SimId(reg)
    @Shared String testSession = 'bill';
    String siteName = 'bill__reg'

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

//    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
//    def 'Submit Pid transaction to Registry simulator'() {
//        when:
//        String siteName = 'bill__reg'
//        TestInstance testId = new TestInstance("15804")
//        List<String> sections = new ArrayList<>()
//        sections.add("section")
//        Map<String, String> params = new HashMap<>()
//        params.put('$patientid$', patientId)
//        boolean stopOnFirstError = true
//
//        and: 'Run pid transaction test'
//        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
//
//        then:
//        true
//        results.size() == 1
//        results.get(0).passed()
//    }

    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit second Pid transaction to Registry simulator'() {
        when:
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId2)
        boolean stopOnFirstError = true

        and: 'Run pid transaction test'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

    def 'Run SQ initialization'() {
        when:
        TestInstance testId = new TestInstance("tc:Initialize_for_Stored_Query")
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId2)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

    def 'Run SQ tests'() {
        when:
        TestInstance testId = new TestInstance("tc:SQ.b")
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId2)   // not used
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

//    def 'Run 11897/approved tests'() {
//        when:
//        TestInstance testId = new TestInstance("11897")
//        List<String> sections = ['approved']
//        Map<String, String> params = new HashMap<>()
//        params.put('$patientid$', patientId2)   // not used
//        boolean stopOnFirstError = true
//
//        and: 'Run'
//        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
//
//        then:
//        results.size() == 1
//        results.get(0).passed()
//    }

}
