package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public class LogRepositoryImpl implements ILoggerIO {
	// Both of these are initialized by LogRepositoryFactory
	File logDir;
	ILoggerIO logger;
	
	// Create through LogRepositoryFactory only
	LogRepositoryImpl() {}
	
	@Override
	public void logOut(XdstestLogId id, LogMap log, File logDir)
			throws XdsException {
		logger.logOut(id, log, logDir);
	}
	
	@Override
	public LogMap logIn(XdstestLogId id, File logDir) throws Exception {
		return logger.logIn(id, logDir);
	}
	
	public File logDir() {
		return logDir;
	}
}
