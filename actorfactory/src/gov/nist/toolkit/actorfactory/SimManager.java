package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simDb.SimDb;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// One instance per Session
public class SimManager {
	static Map<String, SimManager> mgrs = new HashMap<String, SimManager>(); // SimManager for a sessionId
	Map<String, SimDb> simMap = new HashMap<String, SimDb>();  // SimDb for a simulatorId
	List<SimulatorConfig> actorSimulatorConfigs = new ArrayList<SimulatorConfig>();  // for this session
	String sessionId;

	private SimManager(String sessionId) {
		this.sessionId = sessionId;
	}
	
	static public SimManager get(String sessionId) {
		SimManager m = mgrs.get(sessionId);
		if (m == null) {
			m = new SimManager(sessionId);
			mgrs.put(sessionId, m);
		}
		return m;
	}
	
	static public Site getSite(SimulatorConfig config) throws Exception {
		ActorFactory af = getActorFactory(config);
		return af.getActorSite(config, null);
	}
	
	static public List<Site> getSites(Collection<SimulatorConfig> configs) throws Exception {
		List<Site> sites = new ArrayList<Site>();
		for (SimulatorConfig sc : configs) {
			sites.add(SimManager.getSite(sc));
		}
		return sites;
	}

	static public List<Site> getSites(Sites inSites, Collection<SimulatorConfig> configs) throws Exception {
		List<Site> sites = new ArrayList<Site>();
		sites.addAll(inSites.asCollection());
		for (SimulatorConfig sc : configs) {
			sites.add(SimManager.getSite(sc));
		}
		return sites;
	}

	static public ActorFactory getActorFactory(SimulatorConfig config) throws Exception {
		String simtype = config.getType();
		ActorType at = ActorType.findActor(simtype);
		ActorFactory af = ActorFactory.getActorFactory(at);
		return af;
	}
	
	public String sessionId() {
		return sessionId;
	}
	
	public File getCodesFile() throws EnvironmentNotSelectedException, NoSessionException {
		EnvSetting setting  = EnvSetting.getEnvSetting(sessionId);
		return setting.getCodesFile();
	}
	
	public SimDb getSimDb(String simulatorId) throws IOException {
		SimDb db = simMap.get(simulatorId);
		if (db == null) {
			db = new SimDb(Installation.installation().simDbFile(), simulatorId, null, null);
			simMap.put(simulatorId, db);
		}
		return db;
	}
	
//	public void load(List<String> ids) throws IOException, ClassNotFoundException {
//		actorSimulatorConfigs = new SimulatorFactory(this).loadSimulators(ids);
//	}
//	
	// Return sims specific to this session
	public List<SimulatorConfig> simConfigs() throws IOException {
		if (actorSimulatorConfigs == null) {
			actorSimulatorConfigs = new ArrayList<SimulatorConfig>();
		} else {
			purgeDeletedSims();
		}
		return actorSimulatorConfigs;
	}
	
	void purgeDeletedSims() throws IOException {
		List<SimulatorConfig> deletions = null;
		for (SimulatorConfig sc : actorSimulatorConfigs) {
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
			actorSimulatorConfigs.removeAll(deletions);
		}
	}
	
	public void addSimConfig(SimulatorConfig sc) throws IOException {
		simConfigs().add(sc);
	}
	
	public void addSimConfigs(List<SimulatorConfig> scs) throws IOException {
		for (SimulatorConfig sc : scs)
			simConfigs().add(sc);
	}
	
	public void setSimConfigs(List<SimulatorConfig> configs) {
		actorSimulatorConfigs = configs;
	}
	
	public SimulatorConfig getSimulatorConfig(String simId) throws IOException {
		for (SimulatorConfig config : simConfigs()) {
			if (simId.equals(config.getId()) && !config.isExpired())
				return config;
		}
		return null;
	}
	
	public Sites getSites() throws Exception {
		return SimManager.getSites(sessionId);
	}

	static public Sites getSites(String sessionId) throws Exception {
		return getAllSites(sessionId, SiteServiceManager.getSiteServiceManager().getSites());
	}
	
	static public Sites getAllSites(String sessionId, Sites commonSites)  throws Exception{
		Sites sites;
		
		if (commonSites == null)
			sites = new Sites();
		else
			sites = commonSites.clone();
		
		List<SimulatorConfig> actorSimulatorConfigs = get(sessionId).simConfigs();
		
		for (SimulatorConfig asc : actorSimulatorConfigs) {
			if (!asc.isExpired())
				sites.putSite(getSite(asc));
		}
		
		sites.buildRepositoriesSite();
		
		return sites;
	}

	public Sites getAllSites(Sites commonSites)  throws Exception {
		return SimManager.getAllSites(sessionId, commonSites);
	}



}
