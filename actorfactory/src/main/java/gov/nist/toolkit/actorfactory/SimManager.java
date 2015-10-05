package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains the list of loaded SimulatorConfig objects for a
 * single session.
 * @author bill
 *
 */

public class SimManager {
	List<SimulatorConfig> simConfigs = new ArrayList<>();  // for this session
	String sessionId;
	static Logger logger = Logger.getLogger(SimManager.class);


	public SimManager(String sessionId) {
		this.sessionId = sessionId;
		if (Installation.installation().propertyServiceManager().getCacheDisabled()) {
			List<SimId> simIds = loadAllSims();
			logger.debug("Cache disabled - loaded " + simIds.size() + "  sims");
		}
	}

	public List<SimId> loadAllSims() {
		SimDb db = new SimDb();
		List<SimId> simIds = db.getAllSimIds();
		for (SimId simId : simIds) {
			if (!hasSim(simId))
				try {
					simConfigs.add(db.getSimulator(simId));
				}
				catch (Exception e) {
					throw new ToolkitRuntimeException("", e);
				}
		}
		return simIds;
	}
	
//*****************************************
//  These methods would normally belong in class SimulatorConfig but that
//  class is compiled for the client and some of these classes (ActorFactory)
//	do not belong on the client side.
//*****************************************
	static public Site getSite(SimulatorConfig config) throws Exception {
		AbstractActorFactory af = getActorFactory(config);
		return af.getActorSite(config, null);
	}

	static public AbstractActorFactory getActorFactory(SimulatorConfig config) throws Exception {
		String simtype = config.getType();
		ActorType at = ActorType.findActor(simtype);
		AbstractActorFactory af = AbstractActorFactory.getActorFactory(at);
		return af;
	}
//*****************************************


	
	public String sessionId() {
		return sessionId;
	}
	
	public void purge() throws IOException {
		List<SimulatorConfig> deletions = new ArrayList<>();
		for (SimulatorConfig sc : simConfigs) {
			try {
				new SimDb(sc.getId());
			} catch (NoSimException e) {
				deletions.add(sc);
			}
		}
		simConfigs.removeAll(deletions);
	}
	
	public void addSimConfigs(Simulator s) {
		for (SimulatorConfig config : s.getConfigs()) {
			addSimConfig(config);
		}
	}
	
	public void addSimConfig(SimulatorConfig config) {
		delSimConfig(config.getId());
		simConfigs.add(config);
	}
	
	public void setSimConfigs(List<SimulatorConfig> configs) {
		simConfigs = configs;
	}

	public void delSimConfig(SimId simId) {
		int index = findSimConfig(simId);
		if (index != -1)
			simConfigs.remove(index);
	}

	public int findSimConfig(SimId simId) {
		for (int i=0; i<simConfigs.size(); i++) {
			if (simConfigs.get(i).getId().equals(simId)) {
				return i;
			}
		}
		return -1;
	}
	
	public SimulatorConfig getSimulatorConfig(SimId simId) {
		for (SimulatorConfig config : simConfigs) {
			if (simId.equals(config.getId()) && !config.isExpired())
				return config;
		}
		return null;
	}
	
	/**
	 * Get common sites and sim sites defined for this session.
	 * @return
	 * @throws Exception
	 */
	public Sites getAllSites() throws Exception {
		return getAllSites(SiteServiceManager.getSiteServiceManager().getCommonSites());
	}
	
	public Sites getAllSites(Sites commonSites)  throws Exception{
		Sites sites;

		loadAllSims();
		
		if (commonSites == null)
			sites = new Sites();
		else
			sites = commonSites.clone();
		
		for (SimulatorConfig asc : simConfigs) {
			if (!asc.isExpired())
				sites.putSite(getSite(asc));
		}
		
		sites.buildRepositoriesSite();
		
		return sites;
	}

	/**
	 * Return map from simName => simId
	 * @return
	 */
	public Map<String, SimId> getNameMap() {
		Map<String, SimId> nameMap = new HashMap<>();
		
		for (SimulatorConfig sc : simConfigs) {
			String name = sc.getDefaultName();
			SimId id = sc.getId();
			nameMap.put(name, id);
		}
		
		return nameMap;
	}

	/**
	 * Remove simulator config.  Managed as a list for convienence
	 * not because there can be multiple (there can't)
	 * @param simId
	 */
	public void removeSimulatorConfig(SimId simId) {
		List<SimulatorConfig> delete = new ArrayList<SimulatorConfig>();
		for (SimulatorConfig sc : simConfigs) {
			if (sc.getId().equals(simId))
				delete.add(sc);
		}
		simConfigs.removeAll(delete);
	}

	private boolean hasSim(SimId simId) {
		for (SimulatorConfig config : simConfigs) {
			if (config.getId().equals(simId)) return true;
		}
		return false;
	}
}
