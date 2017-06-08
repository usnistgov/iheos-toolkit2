package gov.nist.toolkit.fhirServer.servlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.fhir.support.ResDb;
import gov.nist.toolkit.fhir.support.SimIndexer;
import gov.nist.toolkit.fhirServer.config.SimContext;
import gov.nist.toolkit.fhirServer.config.SimTracker;
import gov.nist.toolkit.fhirServer.provider.OrganizationResourceProvider;
import gov.nist.toolkit.fhirServer.provider.PatientResourceProvider;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This servlet is the actual FHIR server itself
 */
public class ExampleRestfulServlet extends RestfulServer {
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ExampleRestfulServlet.class);

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public ExampleRestfulServlet() {
		super(FhirContext.forDstu2()); // Support DSTU2
	}
	
	/**
	 * This method is called automatically when the
	 * servlet is initializing.
	 */
	@Override
	public void initialize(ServletContext context) throws UnavailableException {
		/*
		 * Two resource providers are defined. Each one handles a specific
		 * type of resource.
		 */
		List<IResourceProvider> providers = new ArrayList<IResourceProvider>();
		providers.add(new PatientResourceProvider());
		providers.add(new OrganizationResourceProvider());
		setResourceProviders(providers);
		
		/*
		 * Use a narrative generator. This is a completely optional step, 
		 * but can be useful as it causes HAPI to generate narratives for
		 * resources which don't otherwise have one.
		 */
//		INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
//		getFhirContext().setNarrativeGenerator(narrativeGen);

		/*
		 * Enable CORS
		 */
		CorsConfiguration config = new CorsConfiguration();
		CorsInterceptor corsInterceptor = new CorsInterceptor(config);
		config.addAllowedHeader("Accept");
		config.addAllowedHeader("Content-Type");
		config.addAllowedOrigin("*");
		config.addExposedHeader("Location");
		config.addExposedHeader("Content-Location");
		config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
		registerInterceptor(corsInterceptor);

		/*
		 * This server interceptor causes the server to return nicely
		 * formatter and coloured responses instead of plain JSON/XML if
		 * the request is coming from a browser window. It is optional,
		 * but can be nice for testing.
		 */
		registerInterceptor(new ResponseHighlighterInterceptor());
		
		/*
		 * Tells the server to return pretty-printed responses by default
		 */
		setDefaultPrettyPrint(true);

		/*
		 * Initialize Toolkit SimDb
		 */

		org.apache.log4j.BasicConfigurator.configure();

		try {
			File warHome = new File(context.getRealPath("/"));
			ourLog.info("...warHome is " + warHome);
			Installation.instance().warHome(warHome);
			ourLog.info("...warHome initialized to " + Installation.instance().warHome());

			ourLog.info("Indexing FHIR simulators...");
			int simsIndexed = SimIndexer.buildAllIndexes();
			ourLog.info("...indexed " + simsIndexed + " sims");
		} catch (Exception e) {
			throw new UnavailableException(ExceptionUtil.exception_details(e));
		}
	}


	/**
	 * Initialize linkage to toolkit needed to handle this request
	 * Overrides method in RestfulServer where we try to keep changes
	 * to a minimum since that is imported code
	 *
	 * This is called before the Interceptors
	 *
	 * @param requestFullPath
	 * @throws Exception
	 */
	@Override
	public void initializeRequestHandler(String requestFullPath) throws Exception {

		if (Installation.instance().externalCache() == null)
			throw new Exception("Server not initialized - External Cache not configured");
		SimId simId = simIdFromURI(requestFullPath);
		if (!ResDb.exists(simId))
			throw new Exception("FHIR Simulator " + simId + " does not exist - request URI was " + requestFullPath);
		SimTracker.setContext(new SimContext(simId));
	}

	/**
	 * Parse request URI and extract Simulator ID
	 * @param requestFullPath
	 * @return
	 */
	private SimId simIdFromURI(String requestFullPath) throws Exception {
		String[] parts = requestFullPath.split("\\/");

		for (int i=0; i<parts.length; i++) {
			if (parts[i].equals("fsim")) {
				if (i+1 >= parts.length)
					throw new Exception("Do not understand URL " + requestFullPath + " - not a valid simulator reference");
				String simIdStr = parts[i+1];
				return new SimId(simIdStr);
			}
		}
		throw new Exception("Do not understand URL " + requestFullPath + " - not a valid simulator reference");
	}


}
