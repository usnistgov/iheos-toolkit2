package gov.nist.toolkit.services.server;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.shared.SimulatorServiceManager;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.TestSession;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.ThreadPoolExhaustedException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This is a second attempt to start a real API.  ClientAPI has not
 * been very useful so far. This one is based on the service managers used by the UI
 */
public class ToolkitApi {
    static Logger logger = Logger.getLogger(ToolkitApi.class);
    private Session session;
    boolean internalUse = true;
    private static ToolkitApi api = null;

    /**
     * Use when running unit tests
     * @return
     */
    public static ToolkitApi forInternalUse() {
        if (api == null) {
            api = new ToolkitApi(TestSession.setupToolkit());
            api.internalUse = true;
            return api;
        }
        if (!api.internalUse) {
            String msg = "Engine initialized for Service Use - cannot reinitialize for Internal Use";
            logger.fatal(msg);
            throw new EngineInitializationException(msg);
        }
        return api;
    }

    /**
     * Use to initialize when implementing a service
     * @return
     */
    public static ToolkitApi forServiceUse() {
        if (api == null) {
            api = new ToolkitApi();
            api.session = new Session(
                    Installation.installation().warHome(),
                    Installation.installation().defaultServiceSessionName());
            api.internalUse = false;
            return api;
        }
        if (api.internalUse) {
            String msg = "Engine initialized for Internal Use - cannot reinitialize for Service Use";
            logger.fatal(msg);
            throw new EngineInitializationException(msg);
        }
        return api;
    }

    /**
     * Constructor
     */
    private ToolkitApi() { }

    private ToolkitApi(Session session) {
        this.session = session;
    }



    /**
     * Create a new simulator.
     * @param actorType - simulator type
     * @param simId - id for the simulator
     * @return - simulator description
     * @throws NoSimException if actortype does not exist
     * @throws ThreadPoolExhaustedException if simulator needs to launch a socket listener and no ports are left in the configuration
     * @throws Exception - if simulator of that id already exists
     */
    public Simulator createSimulator(ActorType actorType, SimId simId) throws Exception {
        simId.setActorType(actorType.getName());
        return simulatorServiceManager().getNewSimulator(actorType.getName(), simId);
    }

    public Simulator createSimulator(SimId simId) throws Exception {
        ActorType actorType = ActorType.findActor(simId.getActorType());
        if (actorType == null) throw new BadSimConfigException("Simulator type " + simId.getActorType() + " does not exist");
        return createSimulator(actorType, simId);
    }

    /**
     * Check whether simulator exists
     * @param simId - id of the simulator
     * @return - boolean
     */
    public boolean simulatorExists(SimId simId) { return new SimDb().exists(simId); }

    /**
     * Delete a simulator
     * @param simId - id of the simulator
     * @throws IOException - probably a bad configuration for toolkit
     * @throws NoSimException - simulator doesn't exist
     */
    public void deleteSimulator(SimId simId) throws IOException, NoSimException { SimDb db = new SimDb(simId); db.delete(); }

    /**
     * Delete as simulator. No error if it doesnt exist.
     * @param simId - id of simulator
     * @throws IOException - probably a bad configuration for toolkit - impossible to tell if delete happened
     */
    public void deleteSimulatorIfItExists(SimId simId) throws IOException {
        try {
            deleteSimulator(simId);
        }
        catch (NoSimException e) {}
    }

    /**
     * Get Site for a simulator.
     * @param simId - id of the simulator
     * @return - site object
     * @throws Exception if there is a problem finding or interpreting the sim
     */
    public Site getSiteForSimulator(SimId simId) throws Exception {
        SimManager simManager = new SimManager(session.getId());
        simManager.loadAllSims();
        SimulatorConfig config = simManager.getSimulatorConfig(simId);
        if (config == null) throw new Exception("Simulator " + simId.toString() + " does not exist");
        return SimManager.getSite(config);
    }

    public SimulatorConfig getConfig(SimId simId) {
        if (session == null) return null;
        SimManager simManager = new SimManager(session.getId());
        simManager.loadAllSims();
        return simManager.getSimulatorConfig(simId);
    }

    public SiteSpec getSiteSpecForSimulator(SimId simId) throws Exception {
        Site site = getSiteForSimulator(simId);
        SiteSpec siteSpec = new SiteSpec();
        siteSpec.setName(site.getName());
        return siteSpec;
    }

    /**
     *
     * @param testSession - name of test session to use or null to use default
     * @param siteName - name of site to target
     * @param testInstance - which test
     * @param sections - list of section names or null to run all
     * @param params - parameter map
     * @param stopOnFirstFailure
     * @return - list of Result objects - one per test step (transaction) run
     * @throws Exception if testSession could not be created
     */
    public List<Result> runTest(String testSession, String siteName, TestInstance testInstance, List<String> sections,  Map<String, String> params, boolean stopOnFirstFailure) throws Exception {
        if (testSession == null) {
            testSession = "API";
            xdsTestServiceManager().addMesaTestSession(testSession);
        }
        SiteSpec siteSpec = new SiteSpec();
        siteSpec.setName(siteName);
        if (session.getMesaSessionName() == null) session.setMesaSessionName(testSession);
        return xdsTestServiceManager().runMesaTest(testSession, siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
    }

    public List<String> getSiteNames(boolean simAlso) {
        return siteServiceManager().getSiteNames(session.getId(), true, simAlso);
    }

    public void setConfig(SimulatorConfig config, String parameterName, String value) {
        new SimulatorApi(session).setConfig(config, parameterName, value);
    }

    public void setConfig(SimulatorConfig config, Properties props) {
        new SimulatorApi(session).setConfig(config, props);
    }

    public void setConfig(SimulatorConfig config, String parameterName, Boolean value) {
        new SimulatorApi(session).setConfig(config, parameterName, value);
    }

    public void createTestSession(String name) throws Exception { xdsTestServiceManager().addMesaTestSession(name); }

    private SimulatorServiceManager simulatorServiceManager() { return  new SimulatorServiceManager(session); }
    private XdsTestServiceManager xdsTestServiceManager() { return session.xdsTestServiceManager(); }
    private SiteServiceManager siteServiceManager() { return SiteServiceManager.getSiteServiceManager(); }
}
