package gov.nist.toolkit.toolkitServices.httpApi

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.server.orchestration.OrchestrationManager
import gov.nist.toolkit.session.server.Session
import org.apache.log4j.Logger

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet

/**
 * wait for the servlets to all startup up and then make sure FHIR Support Server is running.
 */
class ToolkitServicesStartup extends HttpServlet {
    static Logger logger = Logger.getLogger(ToolkitServicesStartup.class);

    class Init extends Thread {

        @Override
        void run() {
                try {
                    logger.info("FHIR Support Server about to start")
                    FhirSupportOrchestrationRequest fRequest = new FhirSupportOrchestrationRequest()
                    fRequest.useExistingState = true
                    fRequest.userName = 'default'
                    Session session = new Session(Installation.instance().warHome())
                    session.setEnvironment('default')

                    FhirSupportOrchestrationResponse fResponse = new OrchestrationManager().buildFhirSupportEnvironment(session, fRequest)
                    String isNot = (!fResponse.isError()) ? '' : 'not'
                    logger.info("FHIR Support Server was ${isNot} started")
                }  catch (Throwable e) {
                    ;
                }
            }
    }

    @Override
    void init(ServletConfig sConfig) {
        new Init().start()
    }

}
