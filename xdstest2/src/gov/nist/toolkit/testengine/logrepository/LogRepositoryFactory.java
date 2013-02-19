package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.actorfactory.SimDb;

import java.io.File;
import java.io.IOException;

public class LogRepositoryFactory {

	public enum Id_type { TIME_ID, SPECIFIC_ID };
	public enum IO_format { JAVA_SERIALIZATION, JACKSON };
	
	public LogRepository getRepository(File location, String user, IO_format format, Id_type idType, String id) throws IOException {
		LogRepository impl = new LogRepository();
		impl.logDir = getLogDir(location, user, idType, id);
		impl.logger = getLoggerIO(format);
		return impl;
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
	
	File getLogDir(File location, String user, Id_type idType, String id) throws IOException {
		if (idType == Id_type.TIME_ID) {
			File logDir = new File(
					location + File.separator + user + 
					File.separator + new SimDb().nowAsFilenameBase()  );
			logDir.mkdirs();
			if (!logDir.exists()) 
				throw new IOException("Cannot create log directory " + logDir.toString());
			if (!logDir.isDirectory()) 
				throw new IOException("Cannot create log directory " + logDir.toString());
			return logDir;
		} else if (idType == Id_type.SPECIFIC_ID) {
			File logDir = new File(
					location + File.separator + user + 
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
