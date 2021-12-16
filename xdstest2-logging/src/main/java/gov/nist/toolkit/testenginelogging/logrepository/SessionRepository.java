package gov.nist.toolkit.testenginelogging.logrepository;

import java.util.logging.Logger;

public class SessionRepository  {
	LogRepository log;
	Logger logger = Logger.getLogger(SessionRepository.class.getName());

//	public SessionRepository(String user) throws IOException {
//		log = new LogRepositoryFactory().getLogRepository(Installation.instance().sessionCache(), user, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
//	}

	public LogRepository getLogRepository() {
		return log;
	}

}
