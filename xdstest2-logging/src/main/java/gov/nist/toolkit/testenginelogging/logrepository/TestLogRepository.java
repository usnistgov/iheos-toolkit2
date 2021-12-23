package gov.nist.toolkit.testenginelogging.logrepository;

import java.io.IOException;

import java.util.logging.Logger;

public class TestLogRepository {
	LogRepository log;
	Logger logger = Logger.getLogger(TestLogRepository.class.getName());

//	public TestLogRepository(String user) throws IOException {
//		log = new LogRepositoryFactory().getLogRepository(Installation.instance().testLogCache(), user, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
//	}

	public LogRepository getNewLogRepository() throws IOException {
		return log;
	}

}
