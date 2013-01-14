package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public interface ILoggerIO {

	public  void logOut(XdstestLogId id, LogMap log, File logDir)
			throws XdsException;

	public  LogMap logIn(XdstestLogId id, File logDir) throws Exception;

}