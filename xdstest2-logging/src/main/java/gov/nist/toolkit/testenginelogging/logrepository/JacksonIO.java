package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public class JacksonIO implements ILoggerIO {

	@Override
	public void logOut(TestInstance id, LogMap log, File logDir)
			throws XdsException {
//		throw new NotImplemented();	
		}

	@Override
	public LogMap logIn(TestInstance id, File logDir) throws Exception {
//		throw new NotImplemented();	
		return null;
		}
}
