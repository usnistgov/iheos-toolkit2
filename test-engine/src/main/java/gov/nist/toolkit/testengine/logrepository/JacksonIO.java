package gov.nist.toolkit.testengine.logrepository;

import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.engine.LogMap;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;

public class JacksonIO implements ILoggerIO {

	@Override
	public void logOut(XdstestLogId id, LogMap log, File logDir)
			throws XdsException {
//		throw new NotImplemented();	
		}

	@Override
	public LogMap logIn(XdstestLogId id, File logDir) throws Exception {
//		throw new NotImplemented();	
		return null;
		}
}
