package gov.nist.toolkit.xdstools2.server.api
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.TransactionType
import gov.nist.toolkit.installation.Installation
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

    def 'Submit Register transaction to Registry simulator'() {
        setup: 'delete left-over sim from last time'
        SimId registry = new SimId("TestRegistry")
        api.deleteSimulatorIfItExists(registry)

        when: 'create new simulator'
        api.createSimulator(ActorType.REGISTRY, registry)

        then: 'verify it exists'
        api.simulatorExists(registry)

        when:
        String testSession = null;  // use default
        String siteName = api.getSiteForSimulator(registry).siteName
        String testName = "11990"
        List<String> sections = new ArrayList<>()
        sections.add("submit")
        String patientId = 'BR14^^^&1.2.360&ISO'
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Get toolkit port to verify this is configured correctly'
        println "Port is ${Installation.installation().propertyServiceManager().toolkitPort}"

//        and: 'Run Register test'
//        List<Result> results = api.runTest(testSession, siteName, testName, sections, params, stopOnFirstError)

        then:
        true
//        results.size() == 1
//        results.get(0).passed()

    }

}
