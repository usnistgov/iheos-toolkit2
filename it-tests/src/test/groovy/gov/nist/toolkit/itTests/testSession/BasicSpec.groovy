package gov.nist.toolkit.itTests.testSession

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.session.server.serviceManager.TestSessionServiceManager
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Shared

class BasicSpec extends ToolkitSpecification {
    @Shared SimDb simDb = new SimDb()
    @Shared TestSessionServiceManager sm = TestSessionServiceManager.INSTANCE
    @Shared String DEFAULT = TestSession.DEFAULT_TEST_SESSION.value

    def setupSpec() {

    }

    def setup() {

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
