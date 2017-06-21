package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.LogIdIOFormat;
import gov.nist.toolkit.results.client.LogIdType;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.log4j.Logger;

import java.io.File;

public class LogRepository  {
    static Logger log = Logger.getLogger(LogRepository.class);

    // Both of these are initialized by LogRepositoryFactory
    ILoggerIO logger;
    File location;
    String user;
    TestInstance id;
    LogIdIOFormat format;
    LogIdType idType;

    // Create through LogRepositoryFactory only
    LogRepository(File location, String user, LogIdIOFormat format, LogIdType idType, TestInstance id) {
        this.location = location;
        this.user = user;
        this.format = format;
        this.idType = idType;
        this.id = id;

        if (id != null) {
            if (location != null)
                id.setLocation(location.toString());
            id.setUser(user);
            id.setFormat(format);
            id.setIdType(idType);
        }
    }

    public String toString() {
        try {
            String s = logDir().toString();
            return s;
        } catch (Exception e) {
            return "LogRepository: bad log directory";
        }
    }

    public void logOut(TestInstance id, LogMapDTO logMapDTO)
            throws XdsException {
        log.debug(String.format("Saving log for %s", id));
        logger.logOut(id, logMapDTO, logDir(id));
    }

    public void logOutIfLinkedToUser(TestInstance id, LogMapDTO logMapDTO) throws XdsException {
            logOut(id, logMapDTO);
    }

    static public LogMapDTO logIn(TestInstance testInstance) throws Exception {
        if (testInstance == null) {
            log.error(ExceptionUtil.here("testId is null"));
            return null;
        }
        try {
            LogRepository repo = LogRepositoryFactory.getLogRepository(new File(testInstance.getLocation()),
                    testInstance.getUser(),
                    testInstance.getFormat(),
                    testInstance.getIdType(),
                    testInstance);
            File dir = repo.logDir(testInstance);
            log.debug(String.format("Loading LogMapDTO for test %s from %s", testInstance, dir));
            return repo.logger.logIn(testInstance, dir);
        } catch (Exception e) {
            log.error("Cannot load " + testInstance.describe() + "\n" + ExceptionUtil.exception_details(e));
            throw e;
        }
    }

    public File logDir() {
        File dir = getLogDir(id);
        if (dir.toString().contains("tc:")) throw new ToolkitRuntimeException("Bad LogDir - " + dir);
        return dir;
    }

    public File logDir(TestInstance id) {
        File dir = getLogDir(id);
        if (dir.toString().contains("tc:")) throw new ToolkitRuntimeException("Bad LogDir - " + dir);
        return dir;
    }

    // assign event including filenames - do not touch file system in case the event is never used
    private void assignEvent(TestInstance testInstance) {
        if (idType != LogIdType.TIME_ID) return;  // doesn't use event structure
        if (testInstance.linkedToLogRepository()) return;
        String event = Installation.nowAsFilenameBase();
        testInstance.setInternalEvent(event);
        File dir = new File(
                location + File.separator + user +
                        File.separator + event);
        log.debug(String.format("Assigning SimResource Dir to test instance %s - %s", testInstance, dir));
        testInstance.setEventDir(dir.toString());
        testInstance.setLocation(location.toString());
        testInstance.setUser(user);
        testInstance.setFormat(format);
        testInstance.setIdType(idType);
    }

    private File getLogDir(/*File location, String user, LogIdType idType,*/ TestInstance id) {
        if (location == null) throw new ToolkitRuntimeException("Internal Error: location is null");
        if (user == null) throw new ToolkitRuntimeException("Internal Error: user is null");
        assignEvent(id);
        if (idType == LogIdType.TIME_ID) {
            File logDir = new File(id.getEventDir());
            logDir.mkdirs();
            if (!logDir.exists())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            if (!logDir.isDirectory())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            return logDir;
        } else if (idType == LogIdType.SPECIFIC_ID) {
            File logDir = new File(location, user);
            if (id != null)  // if null then it cannot be used with logDir() call, must use logDir(String)
                logDir = new File(logDir, id.getId());
            logDir.mkdirs();
            if (!logDir.exists())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            if (!logDir.isDirectory())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            return logDir;
        }
        return null;
    }

    public String getUser() {
        return user;
    }
}
