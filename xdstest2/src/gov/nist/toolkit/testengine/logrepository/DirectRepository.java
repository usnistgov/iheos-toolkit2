package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simDb.SimDb;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class DirectRepository extends LogRepository {
	Logger logger = Logger.getLogger(DirectRepository.class);

	public DirectRepository(String user) {
		super(user);
	}

	@Override
	public LogRepository getNewLogRepository() throws IOException {
		logDir = new File(
				Installation.installation().directSendLogFile(user) + 
				File.separator + new SimDb().nowAsFilenameBase()  );
		logDir.mkdirs();
		if (!logDir.exists()) 
			throw new IOException("Cannot create log directory " + logDir.toString());
		if (!logDir.isDirectory()) 
			throw new IOException("Cannot create log directory " + logDir.toString());
		return this;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
