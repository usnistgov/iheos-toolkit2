package gov.nist.toolkit.itTests.xds.od

import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Document Consumer Actor tests
 */
class XDSDocumentUniqueIdErrorSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String testSession = 'sunil'
    @Shared SimConfig simConfig

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        spi.delete('rep', testSession)

        simConfig = spi.create(
                'rep',
                testSession,
                SimulatorActorType.REPOSITORY,
                'test')

        println "*** describe rep sim: ${simConfig.describe()}"
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.installation().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }


    /**
     * DocCons: retrieve
     * @return
     */
    def 'Run retrieve test'() {
        when:
        String siteName = 'sunil__rep'
        TestInstance testId = new TestInstance("15812")
        List<String> sections = ["Retrieve_OD"]
        Map<String, String> params = new HashMap<>()
        params.put('$repuid$', simConfig.asString(SimulatorProperties.repositoryUniqueId))
        params.put('$od_doc_uid$', "1.1.bogus")
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

}
