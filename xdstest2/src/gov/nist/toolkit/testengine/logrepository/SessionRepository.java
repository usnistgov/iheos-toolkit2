package gov.nist.toolkit.testengine.logrepository;

import org.apache.log4j.Logger;

public class SessionRepository  {
	LogRepository log;
	Logger logger = Logger.getLogger(SessionRepository.class);

//	public SessionRepository(String user) throws IOException {
//		log = new LogRepositoryFactory().getRepository(Installation.installation().sessionCache(), user, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
//	}

	public LogRepository getLogRepository() {
		return log;
	}

}
