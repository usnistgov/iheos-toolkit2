package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;

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
	List<SimulatorConfig> simConfigs = new ArrayList<SimulatorConfig>();  // for this session
	String sessionId;

	public SimManager(String sessionId) {
		this.sessionId = sessionId;
	}
	
//*****************************************
//  These methods would normally belong in class SimulatorConfig but that
//  class is compiled for the client and some of these classes (ActorFactory)
//	do not belong on the client side.
//*****************************************
	static public Site getSite(SimulatorConfig config) throws Exception {
		ActorFactory af = getActorFactory(config);
		return af.getActorSite(config, null);
	}

	static public ActorFactory getActorFactory(SimulatorConfig config) throws Exception {
		String simtype = config.getType();
		ActorType at = ActorType.findActor(simtype);
		ActorFactory af = ActorFactory.getActorFactory(at);
		return af;
	}
//*****************************************


	
	public String sessionId() {
		return sessionId;
	}
	
	// Be careful, this may no longer be relevant??????
	@Deprecated
	void purgeDeletedSims() throws IOException {
		List<SimulatorConfig> deletions = null;
		for (SimulatorConfig sc : simConfigs) {
			String simtype = sc.getType();
			ActorType at = ActorType.findActor(simtype);
			ActorFactory af = ActorFactory.getActorFactory(at);
			af.setSimManager(this);  // doesn't get set otherwise on sim reload
			if (!af.simExists(sc)) {
				if (deletions == null)
					deletions = new ArrayList<SimulatorConfig>();
				deletions.add(sc);
				af.deleteSimulator(sc);
			}
		}
		if (deletions != null) {
			simConfigs.removeAll(deletions);
		}
	}
	
	public void addSimConfigs(Simulator s) {
		simConfigs.addAll(s.getConfigs());
	}
	
	public void addSimConfig(SimulatorConfig config) {
		simConfigs.add(config);
	}
	
	public void setSimConfigs(List<SimulatorConfig> configs) {
		simConfigs = configs;
	}
	
	public SimulatorConfig getSimulatorConfig(String simId) {
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
	public Map<String, String> getNameMap() {
		Map<String, String> nameMap = new HashMap<String, String>();
		
		for (SimulatorConfig sc : simConfigs) {
			String name = sc.getDefaultName();
			String id = sc.getId();
			nameMap.put(name, id);
		}
		
		return nameMap;
	}

	/**
	 * Remove simulator config.  Managed as a list for convienence
	 * not because there can be multiple (there can't)
	 * @param simId
	 */
	public void removeSimulatorConfig(String simId) {
		List<SimulatorConfig> delete = new ArrayList<SimulatorConfig>();
		for (SimulatorConfig sc : simConfigs) {
			if (sc.getId().equals(simId))
				delete.add(sc);
		}
		simConfigs.removeAll(delete);
	}

}
