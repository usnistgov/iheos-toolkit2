package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.adt.ListenerFactory;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Setup and teardown Patient Identity Feed listeners
 * Created by bill on 9/8/15.
 */
public class PatientIdentityFeedServlet extends HttpServlet {
    static Logger logger = Logger.getLogger(PatientIdentityFeedServlet.class);
    File warHome;
//    File simDbDir;

    private static final long serialVersionUID = 1L;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        warHome = new File(config.getServletContext().getRealPath("/"));
        logger.info("...warHome is " + warHome);
        initPatientIdentityFeed();
    }

    public void initPatientIdentityFeed() {
        logger.info("Initializing AdtServlet");
        try {
//            Installation.installation().warHome(warHome);

            logger.info("Initializing ADT Listeners...");

            // Initialize available port range
            List<String> portRange = Installation.installation().propertyServiceManager().getListenerPortRange();
            logger.info("Port range is " + portRange);
            int from = Integer.parseInt(portRange.get(0));
            int to = Integer.parseInt(portRange.get(1));

            ListenerFactory.init(from, to);

            generateCurrentlyConfiguredListeners();
        } catch (ToolkitRuntimeException e) {
            logger.fatal("Cannot start listeners: ", e);
        } catch (Exception e) {
            logger.fatal("Cannot start listeners: ", e);
        }

        logger.info("AdtServlet initialized");
    }

    public static void generateCurrentlyConfiguredListeners() throws IOException, NoSimException, ClassNotFoundException {
        SimDb db = new SimDb();
        List<String> simIds = db.getSimulatorIdsforActorType(ActorType.REGISTRY);
        generateListeners(simIds);
    }

    public static void terminateCurrentlyConfiguredListeners() throws IOException, NoSimException {
        SimDb db = new SimDb();
        List<String> simIds = db.getSimulatorIdsforActorType(ActorType.REGISTRY);
        for (String simId : simIds)
            ListenerFactory.terminate(simId);
    }

    public static void generateListeners(List<String> simIds) throws NoSimException, IOException, ClassNotFoundException {
        for (String simId : simIds) {
            generateListener(simId);
        }
    }

    // returns port
    public static int generateListener(String simId) {
        try {
            return generateListener(GenericSimulatorFactory.loadSimulator(simId));
        } catch (Exception e) {
            throw new ToolkitRuntimeException("Error generating PIF Listener", e);
        }
    }

    public static int generateListener(SimulatorConfig simulatorConfig) {
        String simId = simulatorConfig.getId();
        String portString = portFromSimulatorConfig(simulatorConfig);
        int port = Integer.parseInt(portString);
        ListenerFactory.generateListener(simId, port, new PifHandler());
        return port;
    }

    public static void deleteListener(SimulatorConfig simulatorConfig) {
        String simId = simulatorConfig.getId();
        ListenerFactory.terminate(simId);
    }

    static String portFromSimulatorConfig(SimulatorConfig simulatorConfig) {
        SimulatorConfigElement sce = simulatorConfig.get(RegistryActorFactory.pif_port);
        String simId = simulatorConfig.getId();
        if (sce == null)
            throw new ToolkitRuntimeException("Simulator " + simId + " is a Registry simulator but has no Patient ID Feed port configured");
        String portString = sce.asString();
        if (portString == null || portString.equals(""))
            throw new ToolkitRuntimeException("Simulator " + simId + " is a Registry simulator but has no Patient ID Feed port configured");
        return portString;
    }


    public void destroy() {
        try {
            terminateCurrentlyConfiguredListeners();
        } catch (Exception e) {
            logger.fatal("Cannot terminate listeners: ", e);
        }
    }

}
