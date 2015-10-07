package gov.nist.toolkit.xdstools2.server.api;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.TestSession;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.server.serviceManager.SimulatorServiceManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This is a second attempt to start a real API.  ClientAPI has not
 * been very useful so far. This one is based on the service managers used by the UI
 */
public class ToolkitApi {
    Session session;

    /**
     * Constructor
     */
    public ToolkitApi() {
        this(TestSession.setupToolkit());
    }

    private ToolkitApi(Session session) {
        this.session = session;
    }



    /**
     * Create a new simulator.
     * @param actorType - simulator type
     * @param simId - id for the simulator
     * @return - simulator description
     * @throws Exception - if simulator of that id already exists
     */
    public Simulator createSimulator(ActorType actorType, SimId simId) throws Exception {
        try {
            return simulatorServiceManager().getNewSimulator(actorType.getName(), simId);
        } catch (Exception e) {
            if (e.getMessage().contains("Thread pool exhausted")) {
                // not expecting it to work
                return null;
            } else throw e;
        }
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

    public void setConfig(SimulatorConfig config, String parameterName, Boolean value) {
        new SimulatorApi(session).setConfig(config, parameterName, value);
    }

    public void createTestSession(String name) throws Exception { xdsTestServiceManager().addMesaTestSession(name); }

    private SimulatorServiceManager simulatorServiceManager() { return  new SimulatorServiceManager(session); }
    private XdsTestServiceManager xdsTestServiceManager() { return session.xdsTestServiceManager(); }
    private SiteServiceManager siteServiceManager() { return SiteServiceManager.getSiteServiceManager(); }
}
