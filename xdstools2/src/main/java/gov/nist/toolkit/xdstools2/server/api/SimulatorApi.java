package gov.nist.toolkit.xdstools2.server.api;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.GenericSimulatorFactory;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bill on 6/15/15.
 */
public class SimulatorApi {
    Session session;
    static Logger logger = Logger.getLogger(SimulatorApi.class);


    public SimulatorApi(Session session) {
        this.session = session;
    }

    public Simulator create(String actorTypeName, String simID) throws Exception {
//        return new SimulatorServiceManager(session).getNewSimulator(actorTypeName, simID);
        try {
            SimCache simCache = new SimCache();
            SimManager simMgr = simCache.getSimManagerForSession(session.id(), true);

            Simulator scl = new GenericSimulatorFactory(simMgr).buildNewSimulator(simMgr, actorTypeName, simID);
            simMgr.addSimConfigs(scl);
            logger.info("New simulator for session " + session.id() + ": " + actorTypeName + " ==> " + scl.getIds());
            return scl;
        } catch (EnvironmentNotSelectedException e) {
            logger.error("Environment Not Selected");
            throw new Exception("Environment Not Selected", e);
        } catch (Exception e) {
            logger.error("getNewSimulator:\n" + ExceptionUtil.exception_details(e));
            throw new Exception(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void delete(String simID) throws Exception {
        SimulatorConfig config = new SimulatorConfig(simID, "", null);
//        new SimulatorServiceManager(session).deleteConfig(config);
        try {
            new SimCache().deleteSimConfig(config.getId());
        } catch (IOException e) {
            logger.error("deleteConfig", e);
            throw new Exception(e.getMessage());
        }
    }

    public boolean exists(String simId) {
        return new SimDb().exists(simId);
    }

    public Map<String, String> getSimulatorsAndTypes() {
        Map<String, String> map = new HashMap<String, String>();
        SimDb simHook = new SimDb();

        for (String simId : simHook.getAllSimIds()) {
            String actor = simHook.getActorForSimulator();
            map.put(simId, actor);
        }

        return map;
    }
}
