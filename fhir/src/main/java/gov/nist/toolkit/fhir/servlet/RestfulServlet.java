package gov.nist.toolkit.fhir.servlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import gov.nist.toolkit.fhir.server.resourceProvider.*;
import gov.nist.toolkit.fhir.server.resourceProvider.LocationResourceProvider;
import gov.nist.toolkit.fhir.server.resourceProvider.ObservationResourceProvider;
import gov.nist.toolkit.fhir.server.resourceProvider.PatientResourceProvider;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RestfulServlet extends ToolkitRestfulServer {
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
        resourceProviders.add(new LocationResourceProvider());
        resourceProviders.add(new OrganizationResourceProvider());
        resourceProviders.add(new PractitionerResourceProvider());
        resourceProviders.add(new PractitionerRoleResourceProvider());
        resourceProviders.add(new HealthcareServiceResourceProvider());
        resourceProviders.add(new ObservationResourceProvider());
        resourceProviders.add(new DiagnosticReportResourceProvider());
        resourceProviders.add(new MedicationStatementResourceProvider());
        resourceProviders.add(new MedicationRequestResourceProvider());
        resourceProviders.add(new ProcedureResourceProvider());
        resourceProviders.add(new AllergyIntoleranceResourceProvider());
        resourceProviders.add(new ConditionResourceProvider());
        resourceProviders.add(new ImmunizationResourceProvider());
        resourceProviders.add(new EncounterResourceProvider());
        resourceProviders.add(new CareTeamResourceProvider());
        resourceProviders.add(new CarePlanResourceProvider());
        resourceProviders.add(new CompositionResourceProvider());
        setResourceProviders(resourceProviders);

        /**
         * this interceptor log the request message
         */
        registerInterceptor(new RequestLoggingInterceptor());
    }
}
