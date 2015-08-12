package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.engine.LogMap;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public class LogRepository  {
	// Both of these are initialized by LogRepositoryFactory
//	File logDir;
	ILoggerIO logger;
    File location;
    String user;
    String id;
    LogRepositoryFactory.IO_format format;
    LogRepositoryFactory.Id_type idType;
	
	// Create through LogRepositoryFactory only
	LogRepository(File location, String user, LogRepositoryFactory.IO_format format, LogRepositoryFactory.Id_type idType, String id) {
        this.location = location;
        this.user = user;
        this.format = format;
        this.idType = idType;
        this.id = id;
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
        return getLogDir(location, user, idType, id);
    }

    public File logDir(String id) {
        return getLogDir(location, user, idType, id);
    }

    File getLogDir(File location, String user, LogRepositoryFactory.Id_type idType, String id) {
        if (idType == LogRepositoryFactory.Id_type.TIME_ID) {
            File logDir = new File(
                    location + File.separator + user +
                            File.separator + new SimDb().nowAsFilenameBase()  );
            logDir.mkdirs();
            if (!logDir.exists())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            if (!logDir.isDirectory())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            return logDir;
        } else if (idType == LogRepositoryFactory.Id_type.SPECIFIC_ID) {
            File logDir = new File(location, user);
//            File logDir = new File(
//                    location + File.separator + user +
//                            File.separator + id  );
            if (id != null)  // if null then it cannot be used with logDir() call, must use logDir(String)
                logDir = new File(logDir, id);
            logDir.mkdirs();
            if (!logDir.exists())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            if (!logDir.isDirectory())
                throw new ToolkitRuntimeException("Cannot create log directory " + logDir.toString());
            return logDir;
        }
        return null;
    }

}
