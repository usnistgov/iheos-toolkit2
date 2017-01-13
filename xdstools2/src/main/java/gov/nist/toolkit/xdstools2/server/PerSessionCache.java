package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.installation.Installation;

import java.io.File;

public class PerSessionCache {

	
	static public File getSessionCache(String warHome, String sessionId) {
		Installation.instance().warHome(new File(warHome));
		return Installation.instance().sessionLogFile(sessionId);
	}

//	static public File getSessionCaches(ServletContext servletContext) {
//		return Installation.instance(servletContext).sessionCache();
//	}

}
