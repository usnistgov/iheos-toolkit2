package gov.nist.toolkit.actorfactory;


import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cache of loaded simulators. This is maintained globally (covering all sessions) since one session
 * deleting a common simulator affects other sessions and should
 * be reflected immediately. Simulators are managed by sessionId keeping
 * a Map sessionId => SimManager.
 * @author bill
 *
 */
public class SimCache {
	static Map<String, SimManager> mgrs = new HashMap<String, SimManager>(); // sessionID => SimManager (sims for a session)

	public void put(String sessionId, SimManager sim) {
		mgrs.put(sessionId, sim);
	}

	/**
	 * Make sure this SimulatorConfig is present in the cache. 
	 * @param sessionId
	 * @param sc
	 */
	public void update(String sessionId, SimulatorConfig sc) {
		SimManager sm = mgrs.get(sessionId);
		if (sm == null) {
			sm = new SimManager(sessionId);
			mgrs.put(sessionId, sm);
		}
		String simId = sc.getId();
		SimulatorConfig sc1 = sm.getSimulatorConfig(simId);
		if (sc1 == null) {
			sm.addSimConfig(sc);
		}
	}

	public void update(String sessionId, List<SimulatorConfig> configs) {
		for (SimulatorConfig config : configs) {
			update(sessionId, config);
		}
		
	}
	
	public SimManager getSimManagerForSession(String sessionId) {
		return getSimManagerForSession(sessionId, false);
	}

	public SimManager getSimManagerForSession(String sessionId, boolean create) {
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
	public Set<SimManager> getSimManagersForSim(String simId) throws IOException {
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
	public SimulatorConfig getSimulatorConfig(String simId) throws IOException {
		for (SimManager sman : mgrs.values()) {
			SimulatorConfig sconf = sman.getSimulatorConfig(simId);
			if (sconf != null)
				return sconf;
		}
		return null;
	}

	public void deleteSimConfig(String simId) throws IOException {
		// remove from cache
		for (SimManager sman : mgrs.values()) {
			sman.removeSimulatorConfig(simId);
		}
		try {
			SimDb simdb = new SimDb(simId);
			simdb.delete();
		} catch (NoSimException e) {}
	}
}
