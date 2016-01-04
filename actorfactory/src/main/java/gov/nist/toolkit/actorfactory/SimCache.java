package gov.nist.toolkit.actorfactory;


import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.Site;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Cache of loaded simulators. This is maintained globally (covering all sessions) since one session
 * deleting a common simulator affects other sessions and should
 * be reflected immediately. Simulators are managed by sessionId keeping
 * a Map sessionId => SimManager.
 * @author bill
 *
 */
public class SimCache {
    static Logger logger = Logger.getLogger(SimCache.class);
    static Map<String, SimManager> mgrs = new HashMap<String, SimManager>(); // sessionID => SimManager (sims for a session)

    static public void put(String sessionId, SimManager sim) {
        mgrs.put(sessionId, sim);
    }

    static public Collection<Site> getAllSites() throws Exception {
        Set<Site> sitesSet = new HashSet<>();
        for (SimManager mgr : mgrs.values()) {
            sitesSet.addAll(mgr.getAllSites().asCollection());
        }
        return sitesSet;
    }

    static public String describe() {
        StringBuilder buf = new StringBuilder();
        try {
            for (String sessionId : mgrs.keySet()) {
                SimManager mgr = mgrs.get(sessionId);
                for (Site site : mgr.getAllSites().asCollection()) {
                    buf.append(sessionId).append(':').append(site.getName()).append(' ');
                }
            }
        } catch (Exception e) {}
        return buf.toString();
    }

    /**
     * Make sure this SimulatorConfig is present in the cache.
     * @param sessionId
     * @param sc
     */
    static public void update(String sessionId, SimulatorConfig sc) {
        logger.info("SimCache#update1");
        SimManager sm = mgrs.get(sessionId);
        if (sm == null) {
            sm = new SimManager(sessionId);
            mgrs.put(sessionId, sm);
        }
        SimId simId = sc.getId();
        SimulatorConfig sc1 = sm.getSimulatorConfig(simId);
        if (sc1 == null) {
            logger.info("adding sim to configuration: " + sc);
            sm.addSimConfig(sc);
        }
    }

    static public void update(String sessionId, List<SimulatorConfig> configs) {
        logger.info("SimCache#update2");
        for (SimulatorConfig config : configs) {
            update(sessionId, config);
        }
    }

    static public SimManager getSimManagerForSession(String sessionId) {
        return getSimManagerForSession(sessionId, false);
    }

    static public SimManager getSimManagerForSession(String sessionId, boolean create) {
        SimManager s =  mgrs.get(sessionId);
        if (s == null) {
            s = new SimManager(sessionId);
            put(sessionId, s);
        }
        return s;
    }

    /**
     * Get the SimManagers (each representing a session) that are managing a simulator.
     * A sim can be part of multiple sessions so a set of SimManagers
     * is returned. Underneath, a sim is represented by at most one SimulatorConfig
     * in memory. These objects are shared amongst SimManagers.
     * @param simId
     * @return
     * @throws IOException
     */
    static public Set<SimManager> getSimManagersForSim(SimId simId) throws IOException {
        Set<SimManager> smans = new HashSet<SimManager>();
        for (SimManager sman : mgrs.values()) {
            SimulatorConfig sconf = sman.getSimulatorConfig(simId);
            if (sconf != null) {
                smans.add(sman);
                break;
            }
        }
        return smans;
    }

    /**
     * Retrieve a SimulatorConfig.  Since it can be part of multiple SimManagers
     * but if it is then the multiple SimManagers reference a single instance
     * of SimulatorConfg.  So, just find the first reference to the identified
     * SimulatorConfig and return it.  It is guarenteed unique.
     * @param simId
     * @return
     * @throws IOException
     */
    static public SimulatorConfig getSimulatorConfig(SimId simId) throws IOException {
        for (SimManager sman : mgrs.values()) {
            SimulatorConfig sconf = sman.getSimulatorConfig(simId);
            if (sconf != null)
                return sconf;
        }
        try {
            return GenericSimulatorFactory.loadSimulator(simId, true);
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    static public void deleteSimConfig(SimId simId) throws IOException {
        // remove from cache
        for (SimManager sman : mgrs.values()) {
            sman.removeSimulatorConfig(simId);
        }
        SimulatorConfig config = getSimulatorConfig(simId);
        if (config != null)
            GenericSimulatorFactory.delete(config);
    }

    static public void addToSession(String sessionId, SimulatorConfig simulatorConfig) {
        getSimManagerForSession(sessionId, true).addSimConfig(simulatorConfig);
    }
}
