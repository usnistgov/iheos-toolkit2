package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.engine.LogMap;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import gov.nist.toolkit.xdsexception.XdsException;
import org.apache.log4j.Logger;

import java.io.File;

public class LogRepository  {
    static Logger log = Logger.getLogger(LogRepository.class);

    // Both of these are initialized by LogRepositoryFactory
//	File logDir;
	ILoggerIO logger;
    File location;
    String user;
    String id;
    LogRepositoryFactory.IO_format format;
    LogRepositoryFactory.Id_type idType;
    String timeBasedEventName = null;
	
	// Create through LogRepositoryFactory only
	LogRepository(File location, String user, LogRepositoryFactory.IO_format format, LogRepositoryFactory.Id_type idType, String id) {
        this.location = location;
        this.user = user;
        this.format = format;
        this.idType = idType;
        this.id = id;
        log.debug("LogRepository Constructor");
        try {
            logDir();
            log.debug("LogRepository  - LogDir ok");
        } catch (ToolkitRuntimeException e) {
            log.debug(ExceptionUtil.exception_details(e, "id is " + id));
        }
    }
	
	public String toString() {
		return logDir().toString();
	}
	
	public void logOut(XdstestLogId id, LogMap log)
			throws XdsException {
		logger.logOut(id, log, logDir());
	}
	
	public LogMap logIn(XdstestLogId id) throws Exception {
		return logger.logIn(id, logDir());
	}
	
    public File logDir() {
        File dir = getLogDir(location, user, idType, id);
        if (dir.toString().contains("tc:")) throw new ToolkitRuntimeException("Bad LogDir - " + dir);
//        log.debug(ExceptionUtil.here("LogRepository at " + dir));
        return dir;
    }

    public File logDir(String id) {
        File dir = getLogDir(location, user, idType, id);
        if (dir.toString().contains("tc:")) throw new ToolkitRuntimeException("Bad LogDir - " + dir);
//        log.debug(ExceptionUtil.here("LogRepository at " + dir));
        return dir;
    }

    File getLogDir(File location, String user, LogRepositoryFactory.Id_type idType, String id) {
        if (location == null) throw new ToolkitRuntimeException("Internal Error: location is null");
        if (user == null) throw new ToolkitRuntimeException("Internal Error: user is null");
        if (idType == LogRepositoryFactory.Id_type.TIME_ID) {
            if (timeBasedEventName == null)
                timeBasedEventName = new SimDb().nowAsFilenameBase();
            File logDir = new File(
                    location + File.separator + user +
                            File.separator + timeBasedEventName  );
            logDir.mkdirs();
            if (!logDir.exists())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            if (!logDir.isDirectory())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            return logDir;
        } else if (idType == LogRepositoryFactory.Id_type.SPECIFIC_ID) {
            File logDir = new File(location, user);
            if (id != null)  // if null then it cannot be used with logDir() call, must use logDir(String)
                logDir = new File(logDir, id);
            logDir.mkdirs();
            if (!logDir.exists())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            if (!logDir.isDirectory())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
                log.debug(ExceptionUtil.here(logDir.toString()));
            return logDir;
        }
        return null;
    }

}
