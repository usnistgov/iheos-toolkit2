package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;

/**
 * A LogRepository is a directory where log files can be stored
 * for an event.  This class creates the directory and returns
 * a File handle to the directory.  What goes into the directory
 * @author bill
 *
 */
abstract public class LogRepository1 {
	File logDir = null;

	abstract public LogRepository1 getNewLogRepository(String user) throws IOException;  // must initialize logDir
	abstract protected Logger getLogger();
	
	protected LogRepository1() {
	}
	
	public void logOut(TestInstance id, LogMapDTO log) throws XdsException {
		getLogger().log(Level.FINE,"Writing log " + log.getKeys() + " to " + logDir);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(logFile(id));
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
	
	public LogMapDTO logIn(TestInstance id) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(logFile(id));
			in = new ObjectInputStream(fis);
			LogMapDTO map = (LogMapDTO) in.readObject();
			getLogger().fine("restoring log " + map.getKeys() + " from " + logDir);
			return map;
		} 
		catch (ClassNotFoundException e) {
			getLogger().fine("attempting to restore log " + "from " + logDir);
			throw new XdsInternalException("Cannot create model of type LogMapDTO - class not found",e);
		} finally {
			in.close();
		}
	}

	String logFile(TestInstance id) throws IOException {
		return logDir().toString() + File.separator + id.getId();
	}

	public File logDir() throws IOException {
//		if (logDir == null)
//			getNewLogRepository();
		return logDir; 
	}
	
}
