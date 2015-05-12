package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

public class JavaSerializationIO implements ILoggerIO  {
	Logger logger = Logger.getLogger(JavaSerializationIO.class);

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.logrepository.ILoggerIO#logOut(gov.nist.toolkit.results.client.XdstestLogId, gov.nist.toolkit.testengine.LogMap, java.io.File)
	 */
	@Override
	public void logOut(XdstestLogId id, LogMap log, File logDir) throws XdsException {
		logger.debug("Writing log " + log.getKeys() + " to " + logDir);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(logFile(id, logDir));
			out = new ObjectOutputStream(fos);
			out.writeObject(log);
		} catch (IOException e) {
			throw new XdsInternalException("Cannot write transaction log file", e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				throw new XdsInternalException("Cannot write transaction log file", e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testengine.logrepository.ILoggerIO#logIn(gov.nist.toolkit.results.client.XdstestLogId, java.io.File)
	 */
	@Override
	public LogMap logIn(XdstestLogId id, File logDir) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(logFile(id, logDir));
			in = new ObjectInputStream(fis);
			LogMap map = (LogMap) in.readObject();
			logger.debug("restoring log " + map.getKeys() + " from " + logDir);
			return map;
		} 
		catch (ClassNotFoundException e) {
			logger.debug("attempting to restore log " + "from " + logDir);
			throw new XdsInternalException("Cannot create object of type LogMap - class not found",e);
		} finally {
			in.close();
		}
	}

	String logFile(XdstestLogId id, File logDir) throws IOException {
		return logDir.toString() + File.separator + id.getId();
	}


}
