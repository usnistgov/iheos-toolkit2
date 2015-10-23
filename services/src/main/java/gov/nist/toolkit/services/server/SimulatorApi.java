package gov.nist.toolkit.services.server;

import gov.nist.toolkit.actorfactory.GenericSimulatorFactory;
import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.client.SimExistsException;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by bill on 6/15/15.
 */
public class SimulatorApi {
    Session session;
    static Logger logger = Logger.getLogger(SimulatorApi.class);


    public SimulatorApi(Session session) {
        this.session = session;
    }

    public Simulator create(String actorTypeName, SimId simID) throws Exception {
//        return new SimulatorServiceManager(session).getNewSimulator(actorTypeName, simID);
        try {
            SimDb db = new SimDb();
            if (db.exists(simID))
                throw new SimExistsException("Simulator " + simID + " exists");

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
            throw e;
//            throw new Exception(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void delete(SimId simID) throws Exception {
        SimulatorConfig config = new SimulatorConfig(simID, "", null);
        SimCache simCache = new SimCache();
        SimManager simMgr = simCache.getSimManagerForSession(session.id(), true);
//        new SimulatorServiceManager(session).deleteConfig(config);
        try {
            simCache.deleteSimConfig(config.getId());
            simMgr.purge();
        } catch (IOException e) {
            logger.error("deleteConfig", e);
            throw new Exception(e.getMessage());
        }
    }

    public boolean exists(SimId simId) {
        return new SimDb().exists(simId);
    }

    public void setConfig(SimulatorConfig config, String parameterName, String value) {
        SimManager simMgr = new SimCache().getSimManagerForSession(session.id(), true);
        new GenericSimulatorFactory(simMgr).setConfig(config, parameterName, value);
    }

    public void setConfig(SimulatorConfig config, String parameterName, Boolean value) {
        SimManager simMgr = new SimCache().getSimManagerForSession(session.id(), true);
        new GenericSimulatorFactory(simMgr).setConfig(config, parameterName, value);
    }

}
