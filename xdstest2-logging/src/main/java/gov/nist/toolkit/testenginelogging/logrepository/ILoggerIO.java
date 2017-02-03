package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.io.File;

public interface ILoggerIO {

	void logOut(TestInstance id, LogMapDTO log, File logDir)
			throws XdsException;

	LogMapDTO logIn(TestInstance id, File logDir) throws Exception;

}