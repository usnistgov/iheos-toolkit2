package gov.nist.toolkit.server.server;

import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.File;

/**
 * Create and delete the SessionCache as the servlet is created/destroyed
 * @author bill
 *
 */
public class SessionListener implements HttpSessionListener {
	static Logger logger = Logger.getLogger(SessionListener.class);
			
	void init(HttpSessionEvent arg0) {
	}
	
	public void sessionCreated(HttpSessionEvent arg0) {
		HttpSession s = arg0.getSession();
		String sessionId = s.getId();
		logger.info("Session " + sessionId + " started");
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		HttpSession s = arg0.getSession();
		String sessionId = s.getId();
		logger.info("Session " + sessionId + " stopped");

		File sessionCache = PerSessionCache.getSessionCache(s.getServletContext().getRealPath("/"), sessionId);

		Io.delete(sessionCache);
	}
	
	

}
