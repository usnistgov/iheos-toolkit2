package gov.nist.toolkit.xdstestgui.server;

import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;

import java.io.File;
import java.io.IOException;

public class SessionCache {
	Session session;
	File sessionCacheDir;
	
	public SessionCache(Session session, File sessionCacheDir) {
		this.session = session;
		this.sessionCacheDir = sessionCacheDir;
	}
	
	File getSessionDir() {
		File dir = new File(
				sessionCacheDir.toString() 
		);
		
		dir.mkdirs();
		
		return dir;
	}
	
	 void addLogFile(LogFileContent lf) throws IOException {
		String test = lf.getTest();
		String section = lf.getSection();
		File dir;
		
		if (section == null) {
			dir = new File(getSessionDir() + File.separator + "tests" + File.separator + test);
		} else {
			dir = new File(getSessionDir() + File.separator + "tests" + File.separator + test + File.separator + section);
		}
		dir.mkdirs();
		File logfile = new File(dir.toString() + File.separator + "log.xml");
		Io.stringToFile(logfile, new OMFormatter(lf.getLog()).toString());
	}
	
}
