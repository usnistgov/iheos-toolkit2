package gov.nist.toolkit.grizzlySupport;

import gov.nist.toolkit.adt.ListenerFactory;
import gov.nist.toolkit.installation.server.Installation;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * This servlet is installed for testing purposes only.
 */
public class TestEnvPidPortServlet extends HttpServlet {
    static private Logger logger = Logger.getLogger(TestEnvPidPortServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        logger.info("TestEnvPidPortServlet ---  " + request.getQueryString() + " isTestRunning? " + Installation.isTestRunning());

        if (Installation.isTestRunning()) {

            String cmd = request.getParameter("cmd");
            String simId = request.getParameter("simId");

            if (cmd!=null) {
                logger.info("cmd is: " + cmd);

                if ("allocate".equals(cmd)) {
                    if (simId==null) {
                        response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
                        return;
                    }

                    int pifPort = ListenerFactory.allocatePort(simId);

                    response.setContentType("text/plain");
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter writer = response.getWriter();
                    writer.print(pifPort);
                    writer.close();
                } else if ("terminate".equals(cmd)) {
                   ListenerFactory.terminate(simId);
                    response.setStatus(HttpServletResponse.SC_OK);
                   return;
                } else if ("status".equals(cmd)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
            }

            logger.info("done. ");
        }
    }
}
