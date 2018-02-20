package gov.nist.toolkit.itTests.testpath

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared
/**
 * Created by onh2 on 5/17/16.
 */
class TestkitPathFinderSpec extends ToolkitSpecification{
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    String patientId = 'BR14^^^&1.2.360&ISO'
    String reg = 'test__rep'
    SimId simId = SimIdFactory.simIdBuilder(reg)
    @Shared String testSession = 'test';

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        spi.delete('rep', testSession)

        spi.create(
                'rep',
                testSession,
                SimulatorActorType.REPOSITORY_REGISTRY,
                'testpath')
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        spi.delete('rep','testpath')
        spi.delete('rep','test')
//        server.stop()
//        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true, new TestSession(testSession))}"
        api.createTestSession(testSession)
        if (!api.simulatorExists(simId)) {
            println "Creating sim ${simId}"
            api.createSimulator(ActorType.REPOSITORY_REGISTRY, simId)
        }
    }

    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid transaction to Registry simulator'() {
        when:
        String siteName = 'test__rep'
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


    def 'Run a failed test'() {
        when:
        String siteName = 'test__rep'
        TestInstance testId = new TestInstance("SingleDocument42")
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.withEnvironment('testpath').runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

}