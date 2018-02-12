package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.server.TestSessionFactory;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestSessionServiceManager {
    public static final TestSessionServiceManager INSTANCE = new TestSessionServiceManager();
    private static final Logger logger = Logger.getLogger(TestSessionServiceManager.class);

    public TestSession create() {
        TestSession testSession;

        while (true) {
            testSession = TestSessionFactory.create();
            if (!exists(testSession)) {
                TestSessionFactory.initialize(testSession);
                return testSession;
            }
        }
    }

    public boolean exists(TestSession testSession)  {
        return exists(testSession.getValue());
    }

    public boolean exists(String testSessionName)  {
        if (testSessionName == null) return false;
        return getNames().contains(testSessionName);
    }

    public boolean add(TestSession testSession) throws Exception  {
        String name = testSession.getValue();
        File cache;

        try {
            cache = Installation.instance().propertyServiceManager().getTestLogCache();

            if (name == null || name.equals(""))
                throw new Exception("Cannot add test session with no name");
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

    public List<String> getNames()  {
        List<String> names = new ArrayList<String>();
        File cache;
        try {
            cache = Installation.instance().propertyServiceManager().getTestLogCache();
        } catch (Exception e) {
            logger.error("getMesaTestSessionNames", e);
            throw new ToolkitRuntimeException(e.getMessage());
        }

        String[] namea = cache.list();

        for (int i=0; i<namea.length; i++) {
            File dir = new File(cache, namea[i]);
            if (!dir.isDirectory()) continue;
            if (!namea[i].startsWith("."))
                names.add(namea[i]);

        }

        if (names.size() == 0) {
            names.add("default");
            File def = new File(cache, "default");
            def.mkdirs();
        }

        logger.debug("testSession names are " + names);
        return names;
    }

    public boolean delete(TestSession testSession) throws Exception  {
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

    public void setTestSession(Session session, TestSession testSession) {
        session.setTestSession(testSession);
    }

    public TestSession getTestSession(Session session) {
        if (session == null) return null;
        return session.getTestSession();
    }


}
