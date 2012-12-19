package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simDb.SimDb;

import java.io.File;

import org.apache.log4j.Logger;

public class SessionRepository extends LogRepository {
	Logger logger = Logger.getLogger(SessionRepository.class);

	public SessionRepository(String user) {
		super(user);   // user is sessionId
	}

	@Override
	public LogRepository getNewLogRepository() {
		logDir = new File(
				Installation.installation().sessionLogFile(user) + 
				File.separator + new SimDb().nowAsFilenameBase()  );
		logDir.mkdirs();
		return this;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
