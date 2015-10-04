package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.results.client.TestId;
import gov.nist.toolkit.testenginelogging.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public class JacksonIO implements ILoggerIO {

	@Override
	public void logOut(TestId id, LogMap log, File logDir)
			throws XdsException {
//		throw new NotImplemented();	
		}

	@Override
	public LogMap logIn(TestId id, File logDir) throws Exception {
//		throw new NotImplemented();	
		return null;
		}
}
