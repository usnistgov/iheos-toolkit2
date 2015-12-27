package gov.nist.toolkit.grizzlySupport

import groovy.transform.TypeChecked
import org.apache.log4j.Logger
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.server.model.Resource
import org.glassfish.jersey.server.model.ResourceMethod
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

    static String asCommaSeparatedList(List<String> names) {
        StringBuilder buf = new StringBuilder();

        for (String name : names) {
            if (buf.length() != 0) buf.append(", ");
            buf.append(name);
        }

        return buf.toString();
    }

    public HttpServer getHttpServer() { return server }

//    public GrizzlyWebServer startServer(String port) {
//        GrizzlyWebServer ws = new GrizzlyWebServer(Integer.parseInt(port), Installation.installation().toolkitxFile().toString());
//        try{
////            // Sim Servlet
////            ServletAdapter sa = new ServletAdapter(new SimServlet());
////            ws.addGrizzlyAdapter(sa, new String[]{"/xdstools2"});
//
//            // Jersey web resources
//            ServletAdapter jerseyAdapter = new ServletAdapter();
//            logger.info("Initializing jersey with " + getPackages());
//            jerseyAdapter.addInitParameter("com.sun.jersey.config.property.packages",
//                    asCommaSeparatedList(getPackages()));
//            jerseyAdapter.setContextPath("/xdstools2/rest/");
//            jerseyAdapter.setServletInstance(new ServletContainer());
//            jerseyAdapter.addInitParameter(ServerProperties.TRACING, "ALL");
//            ws.start();
//        } catch (IOException ex){
//            logger.fatal("Grizzly server adaptor failed startup", ex);
//        }
//        return ws;
//    }

//    /**
//     * Main method.
//     * @param args
//     * @throws IOException
//     */
//    public static void main(String[] args) throws IOException {
//        final HttpServer server = startServer("8888");
//        System.out.println(String.format("Jersey app started with WADL available at "
//                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
//        System.in.read();
//        server.stop();
//    }
}

