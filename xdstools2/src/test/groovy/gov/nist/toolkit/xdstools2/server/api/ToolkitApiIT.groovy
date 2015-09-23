package gov.nist.toolkit.xdstools2.server.api
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.TransactionType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.sitemanagement.client.Site
import spock.lang.Specification
/**
 * Created by bill on 9/22/15.
 */
class ToolkitApiIT extends Specification {
    SimId testSim = new SimId('testsim')
    ToolkitApi api;

    def setup() {
        api = new ToolkitApi()
        api.deleteSimulatorIfItExists(testSim)
        println "EC is ${Installation.installation().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }

    def 'Create/delete Simulator'() {
        when: 'create new simulator'
        api.createSimulator(ActorType.REPOSITORY, testSim)

        then: 'check that it exists'
        api.simulatorExists(testSim)

        when: 'try creating it again - must fail - already exists'
        api.createSimulator(ActorType.REPOSITORY, testSim)

        then: 'check'
        thrown Exception

        when: 'trying duplicating it again with a different type'
        api.createSimulator(ActorType.REGISTRY, testSim)

        then: 'must fail'
        thrown Exception

        when: 'delete sim'
        api.deleteSimulator(testSim)

        then: 'check that it is deleted'
        !api.simulatorExists(testSim)
    }

    def 'Get Site for simulator'() {
        when:
        api.createSimulator(ActorType.REPOSITORY, testSim)
        Site site = api.getSiteForSimulator(testSim)

        then:
        notThrown Exception
        site.hasTransaction(TransactionType.PROVIDE_AND_REGISTER)
    }

    // this depends on the simulator mike__reg site file to be downloaded
    // from the running toolkit (where the sim lives) and added
    // to the external cache
    def 'Submit Register transaction to Registry simulator'() {
        when:
        String testSession = null;  // use default
        String siteName = 'mike__reg'
        String testName = "11990"
        List<String> sections = new ArrayList<>()
        sections.add("submit")
        String patientId = 'BR14^^^&1.2.360&ISO'
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run Register test'
        List<Result> results = api.runTest(testSession, siteName, testName, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    def 'Submit Pid transaction to Registry simulator'() {
        when:
        String testSession = null;  // use default
        String siteName = 'mike__reg'
        String testName = "15804"
        List<String> sections = new ArrayList<>()
        sections.add("section")
        String patientId = 'BR14^^^&1.2.360&ISO'   // not used
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run pid transaction test'
        List<Result> results = api.runTest(testSession, siteName, testName, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

}
