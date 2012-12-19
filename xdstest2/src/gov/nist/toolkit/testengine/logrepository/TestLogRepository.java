package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.installation.Installation;

import java.io.IOException;

import org.apache.log4j.Logger;

public class TestLogRepository extends LogRepository {
	Logger logger = Logger.getLogger(TestLogRepository.class);

	public TestLogRepository(String user) {
		super(user);
	}

	@Override
	public LogRepository getNewLogRepository() throws IOException {
		logDir = Installation.installation().testLogFile(user);
		logDir.mkdirs();
		return this;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
