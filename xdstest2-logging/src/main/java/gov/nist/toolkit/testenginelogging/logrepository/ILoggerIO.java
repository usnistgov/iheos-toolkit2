package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.results.client.TestId;
import gov.nist.toolkit.testenginelogging.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public interface ILoggerIO {

	void logOut(TestId id, LogMap log, File logDir)
			throws XdsException;

	LogMap logIn(TestId id, File logDir) throws Exception;

}