package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.io.File;

public class JacksonIO implements ILoggerIO {

	@Override
	public void logOut(TestInstance id, LogMapDTO log, File logDir)
			throws XdsException {
//		throw new NotImplemented();	
		}

	@Override
	public LogMapDTO logIn(TestInstance id, File logDir) throws Exception {
//		throw new NotImplemented();	
		return null;
		}
}
