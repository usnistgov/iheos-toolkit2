package gov.nist.toolkit.toolkitServices.httpApi

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.server.orchestration.OrchestrationManager
import gov.nist.toolkit.session.server.Session
import groovy.transform.TypeChecked
import org.apache.log4j.Logger

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * wait for the servlets to all startup up and then make sure FHIR Support Server is running.
 */
@TypeChecked
class ToolkitServicesStartup extends HttpServlet {
    static Logger logger = Logger.getLogger(ToolkitServicesStartup.class);
    static boolean isDeferred = false

    class Init extends Thread {

        @Override
        void run() {
                try {
                    logger.info("FHIR Support Server about to start")
                    FhirSupportOrchestrationRequest fRequest = new FhirSupportOrchestrationRequest()
                    fRequest.useExistingState = true
                    fRequest.testSession = new TestSession('default')
                    File warHome = Installation.instance().warHome()
                    Session session = new Session(warHome)
                    String startStatus = ""
                    if (!Installation.instance().externalCache()) {
                        isDeferred = true
                       startStatus =
                               "deferred due to an invalid external cache location. FHIR Server will be initialized when the external cache is fixed either through:\n" \
                                + "a) the Toolkit Configuration update UI tool (restart is not necessary)\n" \
                                + "b) the toolkit.property file update (restart is necessary)."
                    } else {
                        isDeferred = false
                        session.setEnvironment('default')
                        FhirSupportOrchestrationResponse fResponse = (FhirSupportOrchestrationResponse) new OrchestrationManager().buildFhirSupportEnvironment(session, fRequest)
                        startStatus = (!fResponse.isError()) ? 'started' : 'not started'
                    }
                    logger.info("FHIR Support Server was ${startStatus}")
                }  catch (Throwable e) {
                    logger.fatal("Failed to launch FHIR Support Server: ${e.getMessage()}")
                    logger.fatal("${e}")
                    e.printStackTrace()
                }
            }
    }

    @Override
    void init(ServletConfig sConfig) {
        new Init().start()
    }

    @Override
    void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String cmd = request.getParameter("cmd")
            if (cmd!=null) {
                if ("isDeferred".equals(cmd)) {
                    response.setContentType("text/plain")
                    response.setStatus(HttpServletResponse.SC_OK)
                    PrintWriter writer = response.getWriter()
                    writer.print(isDeferred)
                    writer.close()
                } else if ("start".equals(cmd)) {
                    if (isDeferred) {
                        response.setStatus(HttpServletResponse.SC_OK)
                       new Init().start()
                    } else {
                        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED)
                    }
                    return
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return
                }
            }
    }
}
