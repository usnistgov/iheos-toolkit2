package gov.nist.toolkit.restTest;


import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    static Logger logger = Logger.getLogger(Main.class);
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8888/xdstools2/rest/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("gov.nist.toolkit.toolkitServices");
        rc.property(ServerProperties.TRACING, "ALL");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
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

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

