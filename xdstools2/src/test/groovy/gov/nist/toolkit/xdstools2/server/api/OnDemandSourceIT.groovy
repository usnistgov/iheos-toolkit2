package gov.nist.toolkit.xdstools2.server.api
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import spock.lang.Specification
/**
 * Runs all Registry tests.
 * To run:
 *    Start toolkit from IntelliJ.  This will establish the EC at a location something like
 *       /Users/bill/dev/toolkit2/xdstools2/target/test-classes/external_cache
 *    On startup this will be echoed in the log window
 *    Using the toolkit [Toolkit Configuration] tool, save this location as the external cache location
 *    Shutdown toolkit
 *    Start toolkit - this will recognize the EC location change
 *    Open Simulation Manager
 *    Select test session named mike (create it if it doesn't exist)
 *    Create a Registry simulator named reg - the full id will be mike__reg
 *    Come back to this file in IntelliJ and click right on the class name and select Run RegistrySelfTestIT
 *    All the self tests will run
 */
class OnDemandSourceIT extends Specification {
    ToolkitApi api;
    String patientId = 'OD14^^^&1.2.560&ISO'
    String reg = 'sunil__reg'
    SimId simId = new SimId(reg)
    String testSession = 'sunil';

    def setup() {
        //api = ToolkitApi.forInternalUse()
        api = new ToolkitApi()
        println "EC is ${Installation.installation().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
        api.createTestSession(testSession)
        if (!api.simulatorExists(simId)) {
            println "Creating sim ${simId}"
            api.createSimulator(ActorType.REGISTRY, simId)
        }
    }


    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid transaction to the Rep-Registry simulator'() {
        when:
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run pid transaction test'
        List<Result> results = api.runTest(null,testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    def 'Run all tests'() {
        when:
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15812")
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(null,testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    /**
     * This section is here, with the other reg/rep tests, because the Retrieve needs the document entry id and the repository id from the previous PnR section.
     * @return
     */
    def 'Run retrieve tests'() {
        when:
        String siteName = 'sunil__odds'
        TestInstance testId = new TestInstance("15812")
        List<String> sections = ["Retrieve"]  //  <--- Run only this section
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(null,testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    /*
    def 'Run SQ test'() {

    }
    */

}
