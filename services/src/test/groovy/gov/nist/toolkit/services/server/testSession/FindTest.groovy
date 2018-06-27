package gov.nist.toolkit.services.server.testSession

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.server.SimulatorServiceManager
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import spock.lang.Shared
import spock.lang.Specification

class FindTest extends Specification {
    @Shared TestSession lynnTestSession = new TestSession('lynn')
    @Shared TestSession billTestSession = new TestSession('bill')
    @Shared TestSession defaultTestSession = new TestSession('default')
    @Shared SimDb simDb = new SimDb()
    @Shared SimulatorServiceManager simulatorServiceManager
    @Shared Session session

    def setupSpec() {
        URL externalCacheMarker = getClass().getResource('/external_cache/external_cache.txt')
        if (externalCacheMarker == null) {
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }
        File externalCache = new File(externalCacheMarker.toURI().path).parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)

        session = UnitTestEnvironmentManager.setupLocalToolkit()
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
