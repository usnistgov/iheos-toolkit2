package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.actorfactory.client.SimId
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
 *
 */
class DicomSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'IDS-AD027-a^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO'
    @Shared String testSession = 'test'
    @Shared String simName = 'rr'
    @Shared SimId simId = new SimId(testSession, simName)
    @Shared String rr = simId.toString()
    @Shared String siteName = rr


    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        new BuildCollections().init(null)

        println "EC is ${Installation.installation().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
        api.createTestSession(testSession)

        spi.delete('rr', testSession)

        if (api.simulatorExists(simId)) {
            println "Opening sim ${simId}"
            api.openSimulator(simId)
        } else {
            println "Creating sim ${simId}"
            api.createSimulator(ActorType.REPOSITORY_REGISTRY, simId)
        }
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
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

    def 'dicom test'() {
        when:
        List<String> sections = new ArrayList<>()
        sections.add("dicom")
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
