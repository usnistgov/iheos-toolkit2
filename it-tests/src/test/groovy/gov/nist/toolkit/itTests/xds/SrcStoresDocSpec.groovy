package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.results.client.AssertionResults
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.resource.SimConfigResource
import spock.lang.Shared
/**
 *
 */
class SrcStoresDocSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SR7^^^&1.2.260&ISO'
    @Shared String rr = 'bill__rr'
    @Shared SimId simId = new SimId(rr)
    @Shared String testSession = 'default'
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
//        System.gc()
        spi.delete('rr', testSession)
        api.deleteSimulatorIfItExists(simId)
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
        api.createTestSession(testSession)
        if (!api.simulatorExists(simId)) {
            println "Creating sim ${simId}"
            api.createSimulator(ActorType.REPOSITORY_REGISTRY, simId)
        }
        sendPid()
    }

    def sendPid() {
        String siteName = rr
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
    }

    def 'submit doc with UC hash'() {
        when:
        String siteName = rr
        TestInstance testId = new TestInstance("11981")
        List<String> sections = ['submit_uppercase_hash']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()

        when:
        Result result = results.get(0)
        List<MetadataCollection> mc = result.getMetadataContent()

        then:
        mc.size() == 1

        when:
        String ssUid = mc.get(0).submissionSets[0].uniqueId

        then:
        ssUid

        when:
        testId = new TestInstance("SourceStoresDocumentValidation")
        sections = ['query', 'retrieve']
        params.clear()
        params.put('$uid$', ssUid)
        session.setSiteSpec(new SiteSpec(rr))
        result = session.xdsTestServiceManager().xdstest(testId, sections, params, null, null, true)
        AssertionResults assertionResults = result.assertions

        then:
        !result.passed()
        assertionResults.getAssertionThatContains('LeftSide Value')
        assertionResults.getAssertionThatContains('RightSide Value')
    }

}
