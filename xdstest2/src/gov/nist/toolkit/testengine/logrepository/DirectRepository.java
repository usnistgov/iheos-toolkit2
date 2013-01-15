package gov.nist.toolkit.testengine.logrepository;

import org.apache.log4j.Logger;

public class DirectRepository {
	Logger logger = Logger.getLogger(DirectRepository.class);
	LogRepository log;
	
//	public DirectRepository(String user) throws IOException {
//		log = new LogRepositoryFactory().getRepository(Installation.installation().directSendLogs(), user, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
//	}

	public LogRepository getLogRepository() {
		return log;
	}
}
