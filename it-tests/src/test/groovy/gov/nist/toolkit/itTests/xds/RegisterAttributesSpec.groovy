package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.results.client.AssertionResults
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.resource.SimConfigResource
import spock.lang.Shared
/**
 *
 */
class RegisterAttributesSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'SR7^^^&1.2.260&ISO'
    @Shared String testSession = prefixNonce('bill')
    @Shared String rr = testSession + '__rr'
    @Shared SimId simId = SimIdFactory.simIdBuilder(rr)
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
//        spi.delete('rr', testSession)
//        api.deleteSimulatorIfItExists(simId)
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true, new TestSession(testSession))}"
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

    def 'bad submissions'() {
        when:
        String siteName = rr
        TestInstance testId = new TestInstance("11998a")
        List<String> sections = []
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)
        for (int i=0; i<results.size(); i++) {
            assert results.get(i).passed()
        }

        then:
        results.size() == 1
    }

}
