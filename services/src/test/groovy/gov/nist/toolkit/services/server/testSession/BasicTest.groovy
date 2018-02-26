package gov.nist.toolkit.services.server.testSession

import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.session.server.serviceManager.TestSessionServiceManager
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import spock.lang.Shared
import spock.lang.Specification

class BasicTest extends Specification {
    @Shared SimDb simDb = new SimDb()
    @Shared TestSessionServiceManager sm = TestSessionServiceManager.INSTANCE
    @Shared String DEFAULT = TestSession.DEFAULT_TEST_SESSION.value

    def setupSpec() {

    }


    def setup() {
        URL externalCacheMarker = getClass().getResource('/external_cache/external_cache.txt')
        if (externalCacheMarker == null) {
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }
        File externalCache = new File(externalCacheMarker.toURI().path).parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)
    }

    def 'test create' () {
        when:
        def testSession = sm.create()

        then:
        sm.inSimDb().contains(testSession.value)
        sm.inActors().contains(testSession.value)
        sm.inTestLogs().contains(testSession.value)
        sm.isConsistant()
    }

    def 'test delete' () {
        when:
        def testSession = sm.create()
        sm.delete(testSession)

        then:
        !sm.inSimDb().contains(testSession.value)
        !sm.inActors().contains(testSession.value)
        !sm.inTestLogs().contains(testSession.value)
        sm.isConsistant()
    }

    def 'test delete all'() {
        when:  'make sure there are some test sessions'
        sm.create()
        sm.create()

        and: 'list and delete all'
        sm.getNames().each { String name ->
            if (name == TestSession.DEFAULT_TEST_SESSION.value) return
            sm.delete(new TestSession(name))
        }

        then:
        sm.inSimDb() == [TestSession.DEFAULT_TEST_SESSION.value] as Set
        sm.inActors() == [TestSession.DEFAULT_TEST_SESSION.value] as Set
        sm.inTestLogs() == [TestSession.DEFAULT_TEST_SESSION.value] as Set
        sm.isConsistant()
    }


}
