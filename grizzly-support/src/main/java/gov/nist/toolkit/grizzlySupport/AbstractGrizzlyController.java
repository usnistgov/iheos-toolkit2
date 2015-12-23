package gov.nist.toolkit.grizzlySupport;


import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;

import java.net.URI;
import java.util.List;

/**
 * Main class.
 *
 */
abstract public class AbstractGrizzlyController {
    static Logger logger = Logger.getLogger(AbstractGrizzlyController.class);
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:%s/xdstools2/rest/";

    public abstract List<String> getPackages();


    String asCommaSeparatedList(List<String> names) {
        StringBuilder buf = new StringBuilder();

        for (String name : names) {
            if (buf.length() != 0) buf.append(", ");
            buf.append(name);
        }

        return buf.toString();
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public HttpServer startServer(String port) {
        String packages = asCommaSeparatedList(getPackages());
        final ResourceConfig rc = new ResourceConfig().packages(packages);
        rc.property(ServerProperties.TRACING, "ALL");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        logger.info("Base URI - " + String.format(BASE_URI, port));
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(String.format(BASE_URI, port)), rc);
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
        return server;
    }

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

