package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class MyServletContextListener implements ServletContextListener {
	static Logger logger = Logger.getLogger(MyServletContextListener.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		cleanup(arg0);
	}

	public void contextInitialized(ServletContextEvent arg0) {
//		cleanup(arg0);
	}

	void cleanup(ServletContextEvent arg0) {
		File sessionCache = new File(arg0.getServletContext().getRealPath("SessionCache"));
		
		if (sessionCache.listFiles() == null)
			return;

		logger.info("Clearing Internal SessionCache");
		for (File f : sessionCache.listFiles()) {
			Io.delete(f);
		}
	}

}
