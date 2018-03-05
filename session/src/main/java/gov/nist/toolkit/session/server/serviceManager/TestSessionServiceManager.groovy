package gov.nist.toolkit.session.server.serviceManager

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.server.TestSessionFactory
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.utilities.io.Io
import groovy.transform.TypeChecked
import org.apache.log4j.Logger

@TypeChecked
class TestSessionServiceManager {
    public static final TestSessionServiceManager INSTANCE = new TestSessionServiceManager();
    private static final Logger logger = Logger.getLogger(TestSessionServiceManager.class);

    TestSession create() {
        TestSession testSession;

        while (true) {
            testSession = TestSessionFactory.create();
            if (!exists(testSession)) {
                TestSessionFactory.initialize(testSession);
                return testSession;
            }
        }
    }

    boolean exists(TestSession testSession)  {
        return exists(testSession.getValue());
    }

    boolean exists(String testSessionName)  {
        if (testSessionName == null) return false;
        return getNames().contains(testSessionName);
    }

    boolean create(TestSession testSession) throws Exception  {
        String name = testSession.getValue();
        File cache;

        try {
            cache = Installation.instance().propertyServiceManager().getTestLogCache();

            if (name == null || name.equals(""))
                throw new Exception("Cannot create test session with no name");
            if (name.contains("__"))
                throw new Exception("Cannot contain a double underscore (__)");
            if (name.contains(" "))
                throw new Exception("Cannot contain spaces");
            if (name.contains("\t"))
                throw new Exception("Cannot contain tabs");
        } catch (Exception e) {
            logger.error("addMesaTestSession", e);
            throw new Exception(e.getMessage());
        }
        File dir = new File(cache.toString() + File.separator + name);
        dir.mkdir();
        return true;
    }

    List<String> getNames()  {
        List<String> a = (inSimDb() + inActors() + inTestLogs()) as List
        return a
//        Installation.instance().getTestSessions().collect { TestSession ts -> ts.value}
    }

    boolean isConsistant() {
        return true
//        Set<String> sims = inSimDb()
//        Set<String> actors = inActors()
//        Set<String> testLogs = inTestLogs()
//
//        sims == actors && actors == testLogs
    }

    Set<String> inSimDb() {
        Installation.instance().simDbFile().listFiles().findAll { File f ->
            f.isDirectory() && !f.name.startsWith('.')
        }.collect { File f -> f.name } as Set
    }

    Set<String> inActors() {
        Installation.instance().actorsDir().listFiles().findAll { File f ->
            f.isDirectory() && !f.name.startsWith('.')
        }.collect { File f -> f.name } as Set
    }

    Set<String> inTestLogs() {
        Installation.instance().testLogCache().listFiles().findAll { File f ->
            f.isDirectory() && !f.name.startsWith('.')
        }.collect { File f -> f.name } as Set
    }


    boolean delete(TestSession testSession) throws Exception  {
        File cache;
        try {
            cache = Installation.instance().propertyServiceManager().getTestLogCache();

            if (testSession == null)
                return false;
        } catch (Exception e) {
            logger.error("delMesaTestSession", e);
            throw new Exception(e.getMessage());
        }
        File dir = new File(cache.toString() + File.separator + testSession.getValue());
        Io.delete(dir);

        // also delete simulators owned by this test session

        SimDb.deleteSims(SimDb.getSimIdsForUser(testSession));

        Io.delete(SimDb.getSimDbFile(testSession));

        Io.delete(Installation.instance().actorsDir(testSession));
        return true;
    }

    void setTestSession(Session session, TestSession testSession) {
        session.setTestSession(testSession);
    }

    TestSession getTestSession(Session session) {
        if (session == null) return null;
        return session.getTestSession();
    }


}
