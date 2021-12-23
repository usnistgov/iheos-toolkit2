package gov.nist.toolkit.session.server.serviceManager

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.server.TestSessionFactory
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.session.client.TestSessionStats
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.installation.shared.ExpirationPolicy
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.utilities.io.Io
import groovy.transform.TypeChecked

import java.util.logging.Level
import java.util.logging.Logger

@TypeChecked
class TestSessionServiceManager {
    public static final TestSessionServiceManager INSTANCE = new TestSessionServiceManager();
    private static final Logger logger = Logger.getLogger(TestSessionServiceManager.class.getName());

    // create with a nonce name
    static TestSession create() {
        TestSession testSession;

        while (true) {
            testSession = TestSessionFactory.create();
            if (!exists(testSession)) {
                TestSessionFactory.initialize(testSession);
                return testSession;
            }
        }
    }

    static boolean exists(TestSession testSession)  {
        return exists(testSession.getValue());
    }

    static boolean exists(String testSessionName)  {
        if (testSessionName == null) return false;
        return getNames().contains(testSessionName);
    }

    static boolean isExpired(TestSession testSession) {
        TestSessionFactory.isExpired(testSession)
    }

    static List<TestSessionStats> getTestSessionStats() {
        getNames().collect { String testSessionName ->
            TestSession testSession = new TestSession(testSessionName)
            TestSessionStats stat = new TestSessionStats()
            stat.testSession = testSession
            stat.expired = isExpired(testSession)
            stat.expires = "${TestSessionFactory.expiresDescription(testSession)}"
            stat.expirationPolicy = TestSessionFactory.getExpirationPolicy(testSession)
            stat.lastUpdated = TestSessionFactory.lastUpdated(testSession).toString()
            stat
        }
    }

    static boolean create(TestSession testSession) throws Exception  {
        String name = testSession.getValue();

        try {
            if (name == null || name.equals(""))
                throw new Exception("Test Session Name cannot create test session with no name");
            if (name.contains("__"))
                throw new Exception("Test Session Name cannot contain a double underscore (__)");
            if (name.contains(" "))
                throw new Exception("Test Session Name cannot contain spaces");
            if (name.contains("\t"))
                throw new Exception("Test Session Name cannot contain tabs");
            if (name.any { String x -> x >= 'A' && x <= 'Z'})
                throw new Exception("Test Session Name cannot contain upper case")
        } catch (Exception e) {
            logger.log(Level.SEVERE, "addMesaTestSession", e);
            throw new Exception(e.getMessage());
        }
        TestSessionFactory.initialize(testSession)

        ExpirationPolicy policy = (testSession == TestSession.DEFAULT_TEST_SESSION) ? ExpirationPolicy.NEVER : ExpirationPolicy.NO_ACTIVITY_FOR_30_DAYS
        TestSessionFactory.setExpirationPolicy(testSession, policy)

        return true
    }

    static List<String> getNames()  {
        TestSessionFactory.getNames()
    }

    static boolean delete(TestSession testSession) throws Exception  {
        File cache;
        if (testSession == Installation.instance().getDefaultTestSession())
            throw new Exception("Cannot delete default Test Session")
        try {
            cache = Installation.instance().propertyServiceManager().getTestLogCache();

            if (testSession == null)
                return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "delMesaTestSession", e);
            throw new Exception(e.getMessage());
        }
        File dir = new File(cache.toString() + File.separator + testSession.getValue());
        Io.delete(dir);

        // also delete simulators owned by this test session

        List<SimId> simIds = SimDb.getSimIdsForUser(testSession);

        // don't delete any sims inherited from default
        List<SimId> onlyMySimIds = new ArrayList<>();
        for (SimId simId : simIds) {
            if (simId.isTestSession(testSession))
                onlyMySimIds.add(simId)
        }

        SimDb.deleteSims(onlyMySimIds);

        Io.delete(SimDb.getSimDbFile(testSession));

        Io.delete(Installation.instance().actorsDir(testSession));

        Io.delete(Installation.instance().testSessionMgmtDir(testSession));

        return true;
    }

    static void setTestSession(Session session, TestSession testSession) {
        session.setTestSession(testSession);
    }

    static TestSession getTestSession(Session session) {
        if (session == null) return null;
        return session.getTestSession();
    }

}
