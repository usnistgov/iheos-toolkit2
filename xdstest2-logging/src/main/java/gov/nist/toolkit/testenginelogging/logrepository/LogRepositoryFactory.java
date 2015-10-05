package gov.nist.toolkit.testenginelogging.logrepository;


import gov.nist.toolkit.results.client.LogIdIOFormat;
import gov.nist.toolkit.results.client.LogIdType;
import gov.nist.toolkit.results.client.TestId;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class LogRepositoryFactory {
	static Logger logger = Logger.getLogger(LogRepositoryFactory.class);

	static public LogRepository getRepository(File location, String user, LogIdIOFormat format, LogIdType idType, TestId id) throws IOException {
		LogRepository impl = new LogRepository(location, user, format, idType, id);
		impl.logger = getLoggerIO(format);
		return impl;
	}
	
	static ILoggerIO getLoggerIO(LogIdIOFormat ioFormat) {
		if (ioFormat == LogIdIOFormat.JAVA_SERIALIZATION) {
			return new JavaSerializationIO();
		} else if (ioFormat == LogIdIOFormat.JACKSON) {
			return new JacksonIO();
		} else {
			return new JavaSerializationIO();
		}
	}
	
}
