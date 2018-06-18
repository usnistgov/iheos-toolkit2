package gov.nist.toolkit.itTests.orch

import gov.nist.toolkit.actortransaction.client.ActorOption
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.services.server.orchestration.OrchEngine
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared

class OrchEngineSimSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi
    @Shared String testSessionName = prefixNonce('bill')
    @Shared TestSession testSession = new TestSession(testSessionName)
    @Shared String rep = 'rep'
    @Shared SimId simId = SimIdFactory.simIdBuilder(testSession, rep)
    @Shared String environment = 'test'
    @Shared ActorOption actorOption = new ActorOption('reporch(xds)')

    def setupSpec() {   // one time setup done when class launched
        SimDb.deleteAllSims(TestSession.DEFAULT_TEST_SESSION)
        SimDb.deleteAllSims(testSession)

        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)


//        spi.delete(rep, testSessionName)

        // This is the SUT
//        spi.create(rep, testSessionName, SimulatorActorType.REPOSITORY, environment)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        api.deleteSimulatorIfItExists(simId)
    }

    OrchEngine engine = new OrchEngine()

    def setup() {
    }

    def 'create reg sim'() {
        when:
        String siteName = 'mine'
        SimId id = SimIdFactory.simIdBuilder(testSession, siteName)
        engine.buildGeneralizedOrchestration(session, environment, testSession, actorOption)
        engine.run(new SiteSpec(Sites.FAKE_SITE_NAME, testSession)) // siteSpec actually never used but needed for interface

        then:
        // id comes from testplan/CreateSim instruction registered against reporch(xds)
        SimDb.exists(id)
    }
}
