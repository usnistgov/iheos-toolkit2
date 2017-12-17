package gov.nist.toolkit.toolkitServices.httpApi

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.server.orchestration.OrchestrationManager
import gov.nist.toolkit.session.server.Session
import org.apache.http.HttpStatus
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.log4j.Logger

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Serves as a way to start things when API is not accessable.  One example
 * is starting the FHIR Support server.  The need for this comes up
 * in low level code that cannot access the API which is at the top of the stack.
 */
class ToolkitServicesServlet extends HttpServlet {
    static Logger logger = Logger.getLogger(ToolkitServicesServlet.class);

    static final String SERVICES_PREFIX = 'services'
    static final String FHIR_SUPPORT_SERVICE = 'fhir_support'
    static final String TEST_SESSION= 'testsession'


    /*

    Start FHIR Support on test session default
    services/start?name=fhir_support;session=default


     */


    @Override
    void init(ServletConfig sConfig) throws ServletException {

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        URI uri = new URI("/foo/?${request.queryString}")

        logger.info("Service request for ${uri}")

        // elements past SERVICES_PREFIX
        List elements = uri.path.split('/')
        elements = elements.subList(elements.findIndexOf { it == SERVICES_PREFIX } + 1, elements.size())

        def params = [:]
        List<NameValuePair> rawParams = URLEncodedUtils.parse(uri, 'UTF-8')
        rawParams.each { params[it.name] = it.value }

        logger.info("params are ${params}")

        String serviceName = params['name']

        logger.info("Service request is for service name ${serviceName}")


        switch (serviceName) {
            case FHIR_SUPPORT_SERVICE:
                FhirSupportOrchestrationRequest fRequest = new FhirSupportOrchestrationRequest()
                fRequest.useExistingState = true
                fRequest.userName = params['session']
                Session session = new Session(Installation.instance().warHome())
                session.setEnvironment('default')
                FhirSupportOrchestrationResponse fResponse = new OrchestrationManager().buildFhirSupportEnvironment(session, fRequest)
                response.status = (fResponse.wasStarted) ? HttpStatus.SC_CREATED : HttpStatus.SC_OK
                break
            default:
                response.status = HttpStatus.SC_NOT_FOUND
                break
        }
    }
}
