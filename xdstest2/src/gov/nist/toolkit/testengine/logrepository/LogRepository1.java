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
	
	public void logOut(XdstestLogId id, LogMap log) throws XdsException {
		getLogger().debug("Writing log " + log.getKeys() + " to " + logDir);
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
	
	public LogMap logIn(XdstestLogId id) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(logFile(id));
			in = new ObjectInputStream(fis);
			LogMap map = (LogMap) in.readObject();
			getLogger().debug("restoring log " + map.getKeys() + " from " + logDir);
			return map;
		} 
		catch (ClassNotFoundException e) {
			getLogger().debug("attempting to restore log " + "from " + logDir);
			throw new XdsInternalException("Cannot create object of type LogMap - class not found",e);
		} finally {
			in.close();
		}
	}

	String logFile(XdstestLogId id) throws IOException {
		return logDir().toString() + File.separator + id.getId();
	}

	public File logDir() throws IOException {
//		if (logDir == null)
//			getNewLogRepository();
		return logDir; 
	}
	
}
