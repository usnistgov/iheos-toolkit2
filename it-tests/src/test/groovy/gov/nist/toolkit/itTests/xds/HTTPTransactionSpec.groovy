package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared
/**
 *
 */
class HTTPTransactionSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SR7^^^&1.2.260&ISO'
    @Shared String testSession = prefixNonce('test')
    @Shared String simName = 'rr'
    @Shared SimId simId = new SimId(new TestSession(testSession), simName)
    @Shared String rr = simId.toString()
    @Shared String siteName = rr


    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        new BuildCollections().init(null)

        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true, new TestSession(testSession))}"
        api.createTestSession(testSession)

        spi.delete('rr', testSession)

        if (api.simulatorExists(simId)) {
            println "Opening sim ${simId}"
            api.openSimulator(simId)
        } else {
            println "Creating sim ${simId}"
            spi.createDocumentRegRep(simId.id, simId.testSession.value, 'default')
        }
    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        server.stop()
//        ListenerFactory.terminateAll()
        api.deleteSimulatorIfItExists(simId)
    }

    TestInstance testId = new TestInstance("PnrXop")
    boolean stopOnFirstError = true

    def setup() {

    }

    def 'Patient Feed'() {
        when:
        List<String> sections = new ArrayList<>()
        sections.add("patientFeed")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)

        and:
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

    def 'XOP test'() {
        when:
        List<String> sections = new ArrayList<>()
        sections.add("Xop")
        Map<String, String> params = new HashMap<>()
//        params.put('$patientid$', patientId)

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()

    }


    def 'NoXOP test'() {
        when:
        List<String> sections = new ArrayList<>()
        sections.add("NoXop")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
        println "Results are " + results.get(0).toString()

        then:
        results.size() == 1
        results.get(0).passed()

    }

    def 'encoded URL test'() {
        when:
        List<String> sections = new ArrayList<>()
        sections.add("encodedURL")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
        println "Results are " + results.get(0).toString()

        then:
        results.size() == 1
        results.get(0).passed()

    }


}
