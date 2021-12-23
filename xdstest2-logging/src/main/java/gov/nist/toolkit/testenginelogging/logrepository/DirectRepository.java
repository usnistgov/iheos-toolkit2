package gov.nist.toolkit.testenginelogging.logrepository;

import java.util.logging.Logger;

public class DirectRepository {
	Logger logger = Logger.getLogger(DirectRepository.class.getName());
	LogRepository log;
	
//	public DirectRepository(String user) throws IOException {
//		log = new LogRepositoryFactory().getLogRepository(Installation.instance().directSendLogs(), user, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
//	}

	public LogRepository getLogRepository() {
		return log;
	}
}
