package gov.nist.toolkit.itTests.orch

import gov.nist.toolkit.actortransaction.client.ActorOption
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.services.server.orchestration.OrchEngine
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared

class OrchEngineSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi
    @Shared String testSessionName = prefixNonce('bill')
    @Shared TestSession testSession = new TestSession(testSessionName)
    @Shared String reg = 'reg'
    @Shared SimId simId = SimIdFactory.simIdBuilder(testSession, reg)
    @Shared String environment = 'test'
    @Shared ActorOption actorOption = new ActorOption('regorch(xds)')

    def setupSpec() {   // one time setup done when class launched
        SimDb.deleteAllSims(TestSession.DEFAULT_TEST_SESSION)
        SimDb.deleteAllSims(testSession)

        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)


        spi.delete(reg, testSessionName)
        spi.create(reg, testSessionName, SimulatorActorType.REGISTRY, environment)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        api.deleteSimulatorIfItExists(simId)
    }

    OrchEngine engine = new OrchEngine()

    def setup() {
        engine.buildGeneralizedOrchestration(session, environment, testSession, actorOption)
    }

    def 'Test Orch Engine'() {
        when:  // sends single PIF to registry
        SiteSpec site = simId.siteSpec
        engine.run(site)

        then:
        noExceptionThrown()
    }

}
