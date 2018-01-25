package gov.nist.toolkit.itTests.testSession

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.services.server.SimulatorServiceManager
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Shared

class FindSpec extends ToolkitSpecification {
    @Shared TestSession lynnTestSession = new TestSession('lynn')
    @Shared TestSession billTestSession = new TestSession('bill')
    @Shared TestSession defaultTestSession = new TestSession('default')
    @Shared SimDb simDb = new SimDb()
    @Shared SimulatorServiceManager simulatorServiceManager

    def setupSpec() {
        simulatorServiceManager = new SimulatorServiceManager(session)
    }

    def 'delete and find'() {
        when:
        simDb.deleteAllSims(defaultTestSession)

        then:
        SimDb.getSimIdsForUser(defaultTestSession) == []
    }

    def 'create and find'() {
        when:
        simDb.deleteAllSims(lynnTestSession)
        simDb.deleteAllSims(billTestSession)
        SimId lynnSimId = SimIdFactory.simIdBuilder('lynn__bill')
        SimId billSimId = SimIdFactory.simIdBuilder('bill__bill')
        Simulator lynnSim = simulatorServiceManager.getNewSimulator(ActorType.REPOSITORY.shortName, lynnSimId)
        Simulator billSim = simulatorServiceManager.getNewSimulator(ActorType.REPOSITORY.shortName, billSimId)

        then:
        SimDb.getSimIdsForUser(lynnTestSession).size() == 1
        SimDb.getSimIdsForUser(lynnTestSession).get(0) == lynnSim.ids[0]
        SimDb.getSimIdsForUser(billTestSession).size() == 1
        SimDb.getSimIdsForUser(billTestSession).get(0) == billSim.ids[0]
    }

    def 'create and find with inheritance from default'() {
        when:
        simDb.deleteAllSims(defaultTestSession)
        simDb.deleteAllSims(billTestSession)
        SimId defaultSimId = SimIdFactory.simIdBuilder('default__bill')
        SimId billSimId = SimIdFactory.simIdBuilder('bill__bill')
        Set createdSims = [defaultSimId, billSimId]
        Simulator defaultSim = simulatorServiceManager.getNewSimulator(ActorType.REPOSITORY.shortName, defaultSimId)
        Simulator billSim = simulatorServiceManager.getNewSimulator(ActorType.REPOSITORY.shortName, billSimId)
        Set billSims = SimDb.getAllSimIds(billTestSession) as Set
        Set defaultSims = SimDb.getAllSimIds(defaultTestSession) as Set

        then:
        billSims.size() == 2 // includes bill and default
        billSims == createdSims
        defaultSims.size() == 1
    }

}
