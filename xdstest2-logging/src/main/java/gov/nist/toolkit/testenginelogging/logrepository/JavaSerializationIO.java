package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import java.util.logging.Logger;

import java.io.*;

public class JavaSerializationIO implements ILoggerIO  {
	private Logger logger = Logger.getLogger(JavaSerializationIO.class.getName());

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testenginelogging.logrepository.ILoggerIO#logOut(gov.nist.toolkit.results.client.XdstestLogId, gov.nist.toolkit.testengine.LogMapDTO, java.io.File)
	 */
	@Override
	public void logOut(TestInstance id, LogMapDTO log, File logDir) throws XdsException {
		logger.fine("Writing logs " + log.getKeys() + " to " + logFile(id, logDir));
		FileOutputStream fos;
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
//				throw new XdsInternalException("Cannot write transaction log file", e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.nist.toolkit.testenginelogging.logrepository.ILoggerIO#logIn(gov.nist.toolkit.results.client.XdstestLogId, java.io.File)
	 */
	@Override
	public LogMapDTO logIn(TestInstance id, File logDir) throws Exception {
		logger.fine("Reading log from " + logFile(id, logDir));
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(logFile(id, logDir));
			in = new ObjectInputStream(fis);
			LogMapDTO map = (LogMapDTO) in.readObject();
			logger.fine("restoring log " + map.getKeys() + " from " + logFile(id, logDir));
			return map;
		} 
		catch (ClassNotFoundException e) {
			logger.fine("attempting to restore log " + "from " + logFile(id, logDir));
			throw new XdsInternalException("Cannot create model of type LogMapDTO - class not found", e);
		} catch (Exception e) {
			logger.severe(ExceptionUtil.here("Cannot load " + logFile(id, logDir)));
			throw e;
		} finally {
			if (in != null)
				in.close();
		}
	}

	String logFile(TestInstance id, File logDir)  {
		return logDir.toString() + File.separator + id.getId();
	}


}
