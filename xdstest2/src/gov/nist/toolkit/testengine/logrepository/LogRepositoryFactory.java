package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simDb.SimDb;

import java.io.File;
import java.io.IOException;

public class LogRepositoryFactory {

	enum Id_type { TIME_ID, SPECIFIC_ID };
	enum IO_format { JAVA_SERIALIZATION, JACKSON };
	
	public LogRepositoryFactory(String user, IO_format format, Id_type idType, String id) throws IOException {
		LogRepositoryImpl impl = new LogRepositoryImpl();
		impl.logDir = getLogDir(user, idType, id);
		impl.logger = getLoggerIO(format);
	}
	
	ILoggerIO getLoggerIO(IO_format ioFormat) {
		if (ioFormat == IO_format.JAVA_SERIALIZATION) {
			return new JavaSerializationIO();
		} else if (ioFormat == IO_format.JACKSON) {
			return new JacksonIO();
		} else {
			return new JavaSerializationIO();
		}
	}
	
	File getLogDir(String user, Id_type idType, String id) throws IOException {
		if (idType == Id_type.TIME_ID) {
			File logDir = new File(
					Installation.installation().directSendLogFile(user) + 
					File.separator + new SimDb().nowAsFilenameBase()  );
			logDir.mkdirs();
			if (!logDir.exists()) 
				throw new IOException("Cannot create log directory " + logDir.toString());
			if (!logDir.isDirectory()) 
				throw new IOException("Cannot create log directory " + logDir.toString());
			return logDir;
		} else if (idType == Id_type.SPECIFIC_ID) {
			File logDir = new File(
					Installation.installation().directSendLogFile(user) + 
					File.separator + id  );
			logDir.mkdirs();
			if (!logDir.exists()) 
				throw new IOException("Cannot create log directory " + logDir.toString());
			if (!logDir.isDirectory()) 
				throw new IOException("Cannot create log directory " + logDir.toString());
			return logDir;
		}
		return null;
	}
}
