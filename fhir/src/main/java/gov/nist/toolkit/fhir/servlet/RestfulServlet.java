package gov.nist.toolkit.fhir.servlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import gov.nist.toolkit.fhir.server.resourceProvider.PatientResourceProvider;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RestfulServlet extends RestfulServer {
    private static final long serialVersionUID = 1L;


    public RestfulServlet() {
        super(FhirContext.forDstu3());
    }
    /**
     * The initialize method is automatically called when the servlet is starting up, so it can
     * be used to configure the servlet to define resource providers, or set up
     * configuration, interceptors, etc.
     */
    @Override
    protected void initialize() throws ServletException {

        /*
         * Takes into account the simulator ID in the server address
         */
        setServerAddressStrategy(new ToolkitServerAddressStrategy());

      /*
       * The servlet defines any number of resource providers, and
       * configures itself to use them by calling
       * setResourceProviders()
       */
        List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
        resourceProviders.add(new PatientResourceProvider());
        setResourceProviders(resourceProviders);

        /**
         * this interceptor log the request message
         */
        registerInterceptor(new RequestLoggingInterceptor());
    }
}
