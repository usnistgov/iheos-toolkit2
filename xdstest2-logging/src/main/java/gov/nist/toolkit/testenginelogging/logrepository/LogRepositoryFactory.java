package gov.nist.toolkit.testenginelogging.logrepository;


import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.client.LogIdIOFormat;
import gov.nist.toolkit.results.client.LogIdType;
import gov.nist.toolkit.results.client.TestInstance;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;

public class LogRepositoryFactory {
	static Logger logger = Logger.getLogger(LogRepositoryFactory.class.getName());

	static public LogRepository getLogRepository(File location, TestSession testSession, LogIdIOFormat format, LogIdType idType, TestInstance id) throws IOException {
		LogRepository impl = new LogRepository(location, testSession, format, idType, id);
		impl.logger = getLoggerIO(format);
		return impl;
	}

	static public LogRepository getLogRepository(File location, String sessionId, LogIdIOFormat format, LogIdType idType, TestInstance id) throws IOException {
		LogRepository impl = new LogRepository(location, sessionId, format, idType, id);
		impl.logger = getLoggerIO(format);
		return impl;
	}

	static private ILoggerIO getLoggerIO(LogIdIOFormat ioFormat) {
		if (ioFormat == LogIdIOFormat.JAVA_SERIALIZATION) {
			return new JavaSerializationIO();
		} else if (ioFormat == LogIdIOFormat.JACKSON) {
			return new JacksonIO();
		} else {
			return new JavaSerializationIO();
		}
	}
	
}
