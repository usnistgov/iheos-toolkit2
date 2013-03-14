package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.util.Hashtable;

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
			
	// sessionID -> HttpSession
	static Hashtable<String, HttpSession> activeSessions = new Hashtable<String, HttpSession>();

	synchronized void addSession(HttpSession s) { activeSessions.put(s.getId(), s); announce("Added a session"); }
	synchronized HttpSession getSession(String id) { return activeSessions.get(id); }
	synchronized void rmSession(String id) { activeSessions.remove(id); announce("Removed a session"); }
	synchronized void rmSession(HttpSession s) { rmSession(s.getId()); }
	synchronized public Hashtable<String, HttpSession> getHttpSessions() { return activeSessions; }
	synchronized public void  deleteAllSessions() {
		announce("Delete all sessions");
		for (String id : activeSessions.keySet()) {
			HttpSession s = activeSessions.get(id);
			s.invalidate();
		}
		activeSessions.clear();
	}
	
	void announce(String reason) { System.out.println(reason + " : found " + activeSessions.size() + " sessions"); }

	void init(HttpSessionEvent arg0) {
		deleteAllSessions();
	}
	
	public void sessionCreated(HttpSessionEvent arg0) {
		HttpSession s = arg0.getSession();
		String sessionId = s.getId();
		logger.info("Session " + sessionId + " started");
		addSession(s);
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		HttpSession s = arg0.getSession();
		String sessionId = s.getId();
		logger.info("Session " + sessionId + " stopped");
		rmSession(s);

		File sessionCache = PerSessionCache.getSessionCache(s.getServletContext().getRealPath("/"), sessionId);

		Io.delete(sessionCache);
	}
	
	

}
