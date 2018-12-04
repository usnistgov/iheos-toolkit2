package gov.nist.toolkit.services.server.testSession

import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.server.TestSessionFactory
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.session.server.serviceManager.TestSessionServiceManager
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Paths

class BasicTest extends Specification {
    @Shared SimDb simDb = new SimDb()
    @Shared TestSessionServiceManager sm = TestSessionServiceManager.INSTANCE

    def setupSpec() {

    }


    def setup() {
        File externalCache = Paths.get(this.getClass().getResource('/').toURI()).resolve('external_cache/external_cache.txt').toFile().parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)
    }

    def 'test create' () {
        when:
        def testSession = sm.create()

        then:
        TestSessionFactory.inSimDb().contains(testSession.value)
        TestSessionFactory.inActors().contains(testSession.value)
        TestSessionFactory.inTestLogs().contains(testSession.value)
    }

    def 'test delete' () {
        when:
        def testSession = sm.create()
        sm.delete(testSession)

        then:
        !TestSessionFactory.inSimDb().contains(testSession.value)
        !TestSessionFactory.inActors().contains(testSession.value)
        !TestSessionFactory.inTestLogs().contains(testSession.value)
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
        TestSessionFactory.inSimDb() == [TestSession.DEFAULT_TEST_SESSION.value] as Set
        TestSessionFactory.inActors() == [TestSession.DEFAULT_TEST_SESSION.value] as Set
        TestSessionFactory.inTestLogs() == [TestSession.DEFAULT_TEST_SESSION.value] as Set
    }


}
