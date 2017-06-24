package gov.nist.toolkit.services.server;

import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.SimExistsException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.GenericSimulatorFactory;
import gov.nist.toolkit.simcommon.server.SimCache;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * API for working with simulators.
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
            if (SimDb.exists(simID))
                throw new SimExistsException("Simulator " + simID + " exists");

            SimManager simMgr = SimCache.getSimManagerForSession(session.id(), true);

            Simulator scl = new GenericSimulatorFactory(simMgr).buildNewSimulator(simMgr, actorTypeName, simID);
            simMgr.addSimConfigs(scl);
            logger.info("New simulator for session " + session.id() + ": " + actorTypeName + " ==> " + scl.getIds());
            return scl;
        } catch (EnvironmentNotSelectedException e) {
            logger.error("Cannot create Simulator - Environment Not Selected");
            throw e;
        } catch (Exception e) {
            logger.error("getNewSimulator:\n" + ExceptionUtil.exception_details(e));
            throw e;
        }
    }

    public void delete(SimId simID) throws Exception {
        logger.info(session.id() + ": Delete simulator " + simID);
//        SimulatorConfig config = new SimulatorConfig(simID, "", null);
        SimManager simMgr = SimCache.getSimManagerForSession(session.id(), true);
        try {
            SimCache.deleteSimConfig(simID);
            simMgr.purge();
        } catch (IOException e) {
            logger.error("deleteConfig", e);
            throw e;
        }
    }

    public boolean exists(SimId simId) {
        return SimDb.exists(simId);
    }

    public void setConfig(SimulatorConfig config, String parameterName, String value) {
        SimManager simMgr = SimCache.getSimManagerForSession(session.id(), true);
        new GenericSimulatorFactory(simMgr).setConfig(config, parameterName, value);
    }

    public void setConfig(SimulatorConfig config, String parameterName, Boolean value) {
        SimManager simMgr = new SimCache().getSimManagerForSession(session.id(), true);
        new GenericSimulatorFactory(simMgr).setConfig(config, parameterName, value);
    }

    public void setConfig(SimulatorConfig config, Properties props) {
//        SimManager simMgr = SimCache.getSimManagerForSession(session.id(), true);
        for (Object okey : props.keySet()) {
            String key = (String) okey;
            Object ovalue = props.get(okey);
            if (ovalue instanceof Boolean) {
                Boolean b = (Boolean) ovalue;
                setConfig(config, key, b);
            } else if (ovalue instanceof String) {
                String s = (String) ovalue;
                setConfig(config, key, s);
            } else
                throw new ToolkitRuntimeException("SimulatorApi.setConfig() - illegal type - value must be Boolean or String, found " + ovalue.getClass().getName());
        }
    }

}
