package gov.nist.toolkit.services.server;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.services.shared.SimulatorServiceManager;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.QueryServiceManager;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.simcommon.client.*;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.simcommon.server.SiteServiceManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdsexception.client.ThreadPoolExhaustedException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * This is a second attempt to start a real API.  ClientAPI has not
 * been very useful so far. This one is based on the service managers used by the UI
 *
 * This class must stay written in Java.  SimulatorsController references it
 * and the Jersey stuff won't recognize it if it is converted to Groovy.
 */

public class ToolkitApi {
    static Logger logger = Logger.getLogger(ToolkitApi.class);
    private Session session;
    private String environmentName;
    boolean internalUse = true;
    private static ToolkitApi api = null;

    /**
     * Use when running unit tests or used in production
     * @return
     */
    public static ToolkitApi forInternalUse() {
        if (api == null) {
            api = new ToolkitApi(UnitTestEnvironmentManager.setupLocalToolkit());
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
            EnvSetting.installServiceEnvironment();
            api.session = new Session(
                    Installation.instance().warHome(),
                    Installation.defaultServiceSessionName());
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

    public static ToolkitApi forNormalUse(Session session) {
        return new ToolkitApi(session);
    }

    /**
     * Constructor
     */
    public ToolkitApi() { }

    private ToolkitApi(Session session) {
        this.session = session;
        logger.info("ToolkitApi using session " + session.id());
    }

    public ToolkitApi withEnvironment(String environmentName){
        this.environmentName=environmentName;
        return this;
    }

    /**
     * Create a new simulator.
     * @param actorType - simulator type
     * @param simId - id for the simulator
     * @return - simulator description
     * @throws ThreadPoolExhaustedException if simulator needs to launch a socket listener and no ports are left in the configuration
     * @throws Exception - if simulator of that id already exists
     */
    public Simulator createSimulator(ActorType actorType, SimId simId) throws Exception {
        simId.setActorType(actorType.getName());
        Simulator sim = simulatorServiceManager().getNewSimulator(actorType.getName(), simId);
        sim.getConfig(0).getConfigEle(SimulatorProperties.environment).setStringValue(simId.getEnvironmentName());
        return sim;
    }

    public Simulator createSimulator(SimId simId) throws Exception {
        ActorType actorType = ActorType.findActor(simId.getActorType());
        logger.info(String.format("Create sim %s of type %s", simId.toString(), simId.getActorType()));
        if (actorType == null) throw new BadSimConfigException("Simulator type " + simId.getActorType() + " does not exist");
        return createSimulator(actorType, simId);
    }

    public Simulator openSimulator(SimId simId) throws Exception {
        SimulatorConfig simConfig = getConfig(simId);
        return new Simulator(simConfig);
    }

    /**
     * Check whether simulator exists
     * @param simId - id of the simulator
     * @return - boolean
     */
    public boolean simulatorExists(SimId simId) { return SimDb.exists(simId); }

    public void saveSimulator(SimulatorConfig config) throws Exception {
        simulatorServiceManager().saveSimConfig(config);
    }

    /**
     * Delete a simulator
     * @param simId - id of the simulator
     * @throws IOException - probably a bad configuration for toolkit
     * @throws NoSimException - simulator doesn't exist
     */
    public void deleteSimulator(SimId simId) throws Exception, NoSimException {
        simulatorServiceManager().deleteConfig(simId);
    }

    /**
     * Delete as simulator. No error if it doesnt exist.
     * @param simId - id of simulator
     * @throws IOException - probably a bad configuration for toolkit - impossible to tell if delete happened
     */
    public void deleteSimulatorIfItExists(SimId simId) throws Exception {
        try {
            deleteSimulator(simId);
        }
        catch (Exception e) {}
    }

    /**
     * Get Site for a simulator.
     * @param simId - id of the simulator
     * @return - site model
     * @throws Exception if there is a problem finding or interpreting the sim
     */
    public Site getSiteForSimulator(SimId simId) throws Exception {
        return SimManager.getSite(getConfig(simId));
    }

    public SimulatorConfig getConfig(SimId simId) throws Exception {
        SimulatorConfig config =  SimDb.getSimulator(simId);
        if (config == null)throw new NoSimException("Simulator not found: " + simId);
        return config;
    }

//    public SiteSpec getSiteSpecForSimulator(SimId simId) throws Exception {
//        Site site = getSiteForSimulator(simId);
//        SiteSpec siteSpec = new SiteSpec();
//        siteSpec.setName(site.getName());
//        return siteSpec;
//    }

    /**
     * get site
     * @param id  String version of SimId
     * @return
     * @throws Exception
     */
    public Site getActorConfig(String id) throws Exception {
        logger.debug("ToolkitApi#getActorConfig for ID: " + id);
        SimId simId = new SimId(id);
        return getSiteForSimulator(simId);
    }

    /**
     *
     * @param testSessionName - name of test session to use or null to use default
     * @param siteName - name of site to target
     * @param testInstance - which test
     * @param sections - list of section names or null to run all
     * @param params - parameter map
     * @param stopOnFirstFailure
     * @return - list of Result objects - one per test step (transaction) run
     * @throws Exception if testSession could not be created
     */
    public List<Result> runTest(String testSessionName, String siteName, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure) throws Exception {
        return runTest(testSessionName,siteName,false,testInstance,sections,params,stopOnFirstFailure);
    }

    public List<Result> runTest(String testSessionName, String siteName, boolean isTls, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure) throws Exception {
        if (testSessionName == null) {
            testSessionName = "API";
            xdsTestServiceManager().addMesaTestSession(testSessionName);
        }
        SiteSpec siteSpec = new SiteSpec();
        siteSpec.setName(siteName);
        siteSpec.setTls(isTls);
        if (session.getMesaSessionName() == null) session.setMesaSessionName(testSessionName);
        // TODO add environment name in following call?
        return xdsTestServiceManager().runMesaTest(environmentName,testSessionName, siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
    }

    public List<Result> runTest(String testSessionName, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure) throws Exception {
        if (testSessionName == null) {
            testSessionName = "API";
            xdsTestServiceManager().addMesaTestSession(testSessionName);
        }
        if (session.getMesaSessionName() == null) session.setMesaSessionName(testSessionName);
        // TODO add environment name in following call?
        return xdsTestServiceManager().runMesaTest(environmentName,testSessionName, siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
    }

    public TestLogs getTestLogs(TestInstance testInstance) {
        return xdsTestServiceManager().getRawLogs(testInstance);
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

    public List<Result> findDocuments(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) {
        return new QueryServiceManager(session).findDocuments2(site, pid, selectedCodes);
    }

    public List<Result> retrieveDocuments(SiteSpec site, Uids uids) throws Exception {
        return new QueryServiceManager(session).retrieveDocument(site, uids);
    }

    public List<String> getSimulatorEventIds(SimId simId, String transaction) throws Exception {
        List<String> ids = new ArrayList<>();
        for (TransactionInstance ti : new SimulatorServiceManager(session).getTransInstances(simId, "", transaction)) {
            ids.add(ti.messageId);
        }
        return ids;
    }

    public String getSimulatorEvent(SimId simId, String transaction, String eventId) throws Exception {
        return new SimulatorServiceManager(session).getTransactionLog(simId, null, transaction, eventId);
    }

    public Session getSession() {
        return session;
    }
}


