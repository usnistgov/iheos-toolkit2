package gov.nist.toolkit.testengine.logrepository;

import java.io.IOException;

import org.apache.log4j.Logger;

public class TestLogRepository {
	LogRepository log;
	Logger logger = Logger.getLogger(TestLogRepository.class);

//	public TestLogRepository(String user) throws IOException {
//		log = new LogRepositoryFactory().getRepository(Installation.installation().testLogFile(), user, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
//	}

	public LogRepository getNewLogRepository() throws IOException {
		return log;
	}

}
