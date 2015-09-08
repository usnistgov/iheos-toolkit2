package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.Installation;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;

/**
 * Setup and teardown Patient Identity Feed listeners
 * Created by bill on 9/8/15.
 */
public class AdtServlet extends HttpServlet {
    static Logger logger = Logger.getLogger(AdtServlet.class);
    File warHome;
    File simDbDir;

    private static final long serialVersionUID = 1L;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("Initializing AdtServlet");
        warHome = new File(config.getServletContext().getRealPath("/"));
        logger.info("...warHome is " + warHome);
        Installation.installation().warHome(warHome);
        simDbDir = Installation.installation().simDbFile();
        logger.info("...simdb = " + simDbDir);

        logger.info("Initializing ADT Listeners...");
        ListenerFactory.init();

        try {
            ListenerFactory.generateCurrentlyConfiguredListeners();
        } catch (Exception e) {
            logger.fatal("Cannot start listeners: ", e);
        }

        logger.info("AdtServlet initialized");
    }

    public void destroy() {
        try {
            ListenerFactory.terminateCurrentlyConfiguredListeners();
        } catch (Exception e) {
            logger.fatal("Cannot terminate listeners: ", e);
        }
    }

}
