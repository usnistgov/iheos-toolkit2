package gov.nist.toolkit.services.server;

import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.*;
import gov.nist.toolkit.simcommon.server.*;
import gov.nist.toolkit.simcommon.server.factories.FilterProxyActorFactory;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

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

    public String saveSite(String sessionId, Site site, TestSession testSession) throws Exception {
        SimId fpSimId = getFilterProxySimId(testSession, site.getSiteName());
        boolean exists = exists(fpSimId);
        boolean needed = site.useFilterProxy;

        if (exists && !needed) {
            delete(fpSimId);
        } else if (!exists && needed) {
            create("fproxy", fpSimId, session.getCurrentEnvironment());
        }
        if (needed) {
            // update proxy config based on Site
            FilterProxyActorFactory.updateEndpoints(fpSimId,site);
        }
        return SiteServiceManager.getSiteServiceManager().saveSite(sessionId, site, testSession);
    }

    public String deleteSite(String sessionId, String siteName, TestSession testSession) throws Exception {
        SimId fpSimId = getFilterProxySimId(testSession, siteName);
        if (exists(fpSimId))
            delete(fpSimId);
        return SiteServiceManager.getSiteServiceManager().deleteSite(sessionId, siteName, testSession);
    }

    static SimId getFilterProxySimId(TestSession testSession,String siteName) {
        String proxyName = siteName + "_FilterProxy";
        return SimIdFactory.simIdBuilder(testSession, proxyName);
    }

    // End of wrapper

    public Simulator create(String actorTypeName, SimId simID, String environment) throws Exception {
//        return new SimulatorServiceManager(session).getNewSimulator(actorTypeName, simID);
        try {
            if (SimDb.exists(simID))
                throw new SimExistsException("Simulator " + simID + " exists");

            SimManager simMgr = SimCache.getSimManagerForSession(session.id(), true);

            Simulator scl = new GenericSimulatorFactory(simMgr).buildNewSimulator(simMgr, actorTypeName, simID, environment);
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

    public void delete(SimId simId) throws Exception {
        logger.info(session.id() + ": Delete simulator " + simId);
                GenericSimulatorFactory.delete(simId);
    }

    public boolean exists(SimId simId) {
        return SimDb.exists(simId);
    }

    public void setConfig(SimulatorConfig config, String parameterName, String value) {
        SimManager simMgr = SimCache.getSimManagerForSession(session.id(), true);
        new GenericSimulatorFactory(simMgr).setConfig(config, parameterName, value);
    }

    public void setConfig(SimulatorConfig config, String parameterName, Boolean value) {
        SimManager simMgr = SimCache.getSimManagerForSession(session.id(), true);
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
