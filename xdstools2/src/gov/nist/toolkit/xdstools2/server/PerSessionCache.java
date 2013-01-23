package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.installation.Installation;

import java.io.File;

import javax.servlet.ServletContext;

public class PerSessionCache {

	
	static public File getSessionCache(String warHome, String sessionId) {
		Installation.installation().warHome(new File(warHome));
		return Installation.installation().sessionLogFile(sessionId);
	}

	static public File getSessionCaches(ServletContext servletContext) {
		return Installation.installation(servletContext).sessionCache();
	}

}
