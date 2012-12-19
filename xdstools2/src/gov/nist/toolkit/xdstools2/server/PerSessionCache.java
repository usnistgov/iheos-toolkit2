package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.testengine.RawLogCache;

import java.io.File;

import javax.servlet.ServletContext;

public class PerSessionCache {

	static public void initRawLogCache(ServletContext servletContext, String sessionId) {
//		new RawLogCache(Installation.installation(servletContext).sessionLogFile(sessionId));

		
//				new File(
//				servletContext.getRealPath("/") 
//				+ File.separator + "SessionCache"
//				+ File.separator + sessionId)
//		);
	}
	
	static public File getSessionCache(String warHome, String sessionId) {
		Installation.installation().warHome(new File(warHome));
		return Installation.installation().sessionLogFile(sessionId);
//		return new File(
//				warHome 
//				+ File.separator + "SessionCache"
//				+ File.separator + sessionId
//				);
	}

	static public File getSessionCaches(ServletContext servletContext) {
		return Installation.installation(servletContext).sessionCache();
//		return new File(
//				servletContext.getRealPath("/") 
//				+ File.separator + "SessionCache"
//				);
	}

}
