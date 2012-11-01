package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * Create and delete the SessionCache as the servlet is created/destroyed
 * @author bill
 *
 */
public class SessionListener implements HttpSessionListener {
	static Logger logger = Logger.getLogger(SessionListener.class);
		
	String sessionId;
	HttpSession s;
	
	void init(HttpSessionEvent arg0) {
		s = arg0.getSession();
		sessionId = s.getId();
	}
	
	public void sessionCreated(HttpSessionEvent arg0) {
		init(arg0);
		logger.info("Session " + sessionId + " started");
				
		PerSessionCache.initRawLogCache(s.getServletContext(), sessionId);
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		init(arg0);
		logger.info("Session " + sessionId + " stopped");

		File sessionCache = PerSessionCache.getSessionCache(s.getServletContext().getRealPath("/"), sessionId);

		Io.delete(sessionCache);
	}
	
	

}
