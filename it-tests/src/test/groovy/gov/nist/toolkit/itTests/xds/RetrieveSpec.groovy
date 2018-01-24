package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.resource.SimConfigResource
import spock.lang.Shared

/**
 * Test the Retrieve transaction
 */
class RetrieveSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SR7^^^&1.2.260&ISO'
    @Shared String reg = 'sunil__rr'
    @Shared SimId simId = SimIdFactory.simIdBuilder(reg)
    @Shared String testSession = 'sunil'
    @Shared String repUid = ''

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        spi.delete('rr', testSession)

        SimConfigResource rrConfig = spi.create(
                'rr',
                testSession,
                SimulatorActorType.REPOSITORY_REGISTRY,
                'test')

        repUid = rrConfig.asString(SimulatorProperties.repositoryUniqueId)

        println "*** Repository UID is ***:   ${repUid}"

    }

    def cleanupSpec() {  // one time shutdown when everything is done
        spi.delete('rr', testSession)
        api.deleteSimulatorIfItExists(simId)
        server.stop()
        ListenerFactory.terminateAll()
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
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        params.put('$repuid$', repUid)
        boolean stopOnFirstError = true

        and: 'Run pid transaction test'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    def 'Setup test with submissions'() {
        when:
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15816")
        List<String> sections = ['Register_Stable', 'PnR']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        params.put('$repuid$', repUid)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

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
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15816")
        List<String> sections = ["Retrieve_Doc", 'Retrieve_Bad_Doc_Uid', 'Retrieve_Partial_Uid']
//        List<String> SECTIONS = ['Retrieve_Partial_Uid']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        params.put('$repuid$', repUid)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()

        //println "size: ${results.size()}, pass: ${results.get(0).passed()}"
    }

}