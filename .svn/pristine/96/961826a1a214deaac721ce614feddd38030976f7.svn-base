package gov.nist.toolkit.testengine;

import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RawLogCache {
	File logCache;
	
	public RawLogCache(File parentDir) {
		logCache = new File(parentDir + File.separator + "RawLogCache");
		logCache.mkdirs();
	}
	
	public File getDir() { return logCache; }

	public LogMap getLog(XdstestLogId id) throws Exception {
		return logIn(id);
	}
	
	String logFile(XdstestLogId id) {
		if (logCache == null)
			return "";
		return logCache.toString() + File.separator + id.getId();
	}
	
	public void logOut(XdstestLogId id, LogMap log) throws XdsException {
		if (logCache == null)
			return;
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(logFile(id));
			out = new ObjectOutputStream(fos);
			out.writeObject(log);
			out.close();
		} catch (IOException e) {
			throw new XdsInternalException("Cannot write transaction log file", e);
		}
	}
	
	LogMap logIn(XdstestLogId id) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(logFile(id));
			in = new ObjectInputStream(fis);
			LogMap map = (LogMap) in.readObject();
			in.close();
			return map;
		} 
		catch (ClassNotFoundException e) {
			throw new XdsInternalException("Cannot create object of type LogMap - class not found",e);
		}
	}


}
