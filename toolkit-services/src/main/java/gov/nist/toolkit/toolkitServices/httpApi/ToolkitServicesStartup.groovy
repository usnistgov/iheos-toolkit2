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
import javax.servlet.http.HttpServlet

/**
 * wait for the servlets to all startup up and then make sure FHIR Support Server is running.
 */
@TypeChecked
class ToolkitServicesStartup extends HttpServlet {
    static Logger logger = Logger.getLogger(ToolkitServicesStartup.class);

    class Init extends Thread {

        @Override
        void run() {
                try {
                    logger.info("FHIR Support Server about to start")
                    FhirSupportOrchestrationRequest fRequest = new FhirSupportOrchestrationRequest()
                    fRequest.useExistingState = true
                    fRequest.testSession = new TestSession('default')
                    Session session = new Session(Installation.instance().warHome())
                    session.setEnvironment('default')

                    FhirSupportOrchestrationResponse fResponse = (FhirSupportOrchestrationResponse) new OrchestrationManager().buildFhirSupportEnvironment(session, fRequest)
                    String isNot = (!fResponse.isError()) ? '' : 'not'
                    logger.info("FHIR Support Server was ${isNot} started")
                }  catch (Throwable e) {
                    logger.fatal("Failed to launch FHIR Support Server: ${e.getMessage()}")
                }
            }
    }

    @Override
    void init(ServletConfig sConfig) {
        new Init().start()
    }

}
