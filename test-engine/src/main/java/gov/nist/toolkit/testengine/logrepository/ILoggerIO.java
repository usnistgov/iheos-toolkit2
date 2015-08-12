package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.engine.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public interface ILoggerIO {

	void logOut(XdstestLogId id, LogMap log, File logDir)
			throws XdsException;

	LogMap logIn(XdstestLogId id, File logDir) throws Exception;

}