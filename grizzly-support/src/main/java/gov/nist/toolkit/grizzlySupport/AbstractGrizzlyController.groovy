package gov.nist.toolkit.grizzlySupport

import gov.nist.toolkit.fhir.servlet.FhirServletFilter
import gov.nist.toolkit.fhir.simulators.servlet.HttpSimServlet
import gov.nist.toolkit.fhir.servlet.RestfulServlet
import gov.nist.toolkit.fhir.simulators.servlet.SimServlet
import groovy.transform.TypeChecked
import org.apache.log4j.Logger
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.servlet.FilterRegistration
import org.glassfish.grizzly.servlet.ServletRegistration
import org.glassfish.grizzly.servlet.WebappContext
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.server.model.Resource
import org.glassfish.jersey.server.model.ResourceMethod

import javax.servlet.DispatcherType

/**
 *
 *
 */
@TypeChecked
abstract public class AbstractGrizzlyController {
    static Logger logger = Logger.getLogger(AbstractGrizzlyController.class);
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:%s/xdstools2/rest/";
    HttpServer server = null;

    public abstract List<String> getPackages();

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return this instance
     */
    public AbstractGrizzlyController start(String port) {
        String packages = asCommaSeparatedList(getPackages());
        final ResourceConfig rc = new ResourceConfig().packages(packages);
        rc.property(ServerProperties.TRACING, "ALL");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        logger.info("Base URI - " + String.format(BASE_URI, port));
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(String.format(BASE_URI, port)), rc);
        boolean loaded = false;
        ServerConfig config = rc.getConfiguration();
        for (Resource r : config.getResources()) {
            for (ResourceMethod m : r.getAllMethods()) {
                loaded = true;
                logger.info(m);
            }
        }
        for (Class<?> c : rc.getClasses()) {
            loaded = true;
            logger.info("Resource Loaded - " + c.getName());
        }
        if (!loaded) logger.fatal("No resources loaded");
        logger.info("Started...");
        return this;
    }

    public void stop() {
        if (server) server.shutdown()
    }

    AbstractGrizzlyController withAxis2() {
        File axis2 = new File(getClass().getResource('/axis2.xml').file)
        System.getProperties().setProperty('axis2.xml', axis2.toString())
        System.getProperties().setProperty('axis2.repo', axis2.parentFile.toString())
        this
    }

    AbstractGrizzlyController withSimServlet() {
        final WebappContext tools2 = new WebappContext("xdstools2","")
        final ServletRegistration sims = tools2.addServlet("SimServlet",new SimServlet());
        sims.addMapping('sim/*')
        final ServletRegistration httpSims = tools2.addServlet("HttpSimServlet", new HttpSimServlet());
        httpSims.addMapping('httpsim/*')
        tools2.deploy(getHttpServer())
        this
    }

    AbstractGrizzlyController withFhirServlet() {
        final WebappContext tools2 = new WebappContext("fhir","")
        FilterRegistration fhirFilter = tools2.addFilter("FhirLoggingFilter", FhirServletFilter.class)
        fhirFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), '/*')
        fhirFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), '/fsim/*')
        fhirFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), '/xdstools2/fsim/*')

        final ServletRegistration sims = tools2.addServlet("fhir", new RestfulServlet());
        sims.addMapping('/xdstools2/fhir/*')
        sims.addMapping('xdstool2/fsim/*')
        sims.addMapping('fsim/*')
        sims.setLoadOnStartup(1)
        tools2.deploy(getHttpServer())
        this
    }

    AbstractGrizzlyController withToolkit() {
        withAxis2().withSimServlet()
    }

    static String asCommaSeparatedList(List<String> names) {
        StringBuilder buf = new StringBuilder();

        for (String name : names) {
            if (buf.length() != 0) buf.append(", ");
            buf.append(name);
        }

        return buf.toString();
    }

    public HttpServer getHttpServer() { return server }

}

