package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import groovy.transform.Synchronized;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Maintains the list of loaded SimulatorConfig objects for a
 * single session.
 *
 * All methods that reference simConfigs are labeled @Synchronized
 * @author bill
 *
 */

public class SimManager {
	private List<SimulatorConfig> simConfigs = new ArrayList<>();  // for this session
	private String sessionId;  // this is never used internally.  Other classes use it through the getter.
	private static Logger logger = Logger.getLogger(SimManager.class);


	public SimManager(String sessionId) {
		this.sessionId = sessionId;
		if (Installation.instance().propertyServiceManager().getCacheDisabled()) {
			List<SimId> simIds = loadAllSims();
			logger.debug("Cache disabled - loaded " + simIds.size() + "  sims");
		}
	}

	@Synchronized
	public List<SimId> loadAllSims() {
		SimDb db = new SimDb();
		List<SimId> simIds = new SimDb().getAllSimIds();
		List<SimId> loadedSimIds = new ArrayList<>();
		for (SimId simId : simIds) {
			try {
				if (!hasSim(simId))
					try {
						logger.info("Load sim " + simId);
						simConfigs.add(SimDb.getSimulator(simId));
						loadedSimIds.add(simId);
					}
					catch (Exception e) {
						throw new ToolkitRuntimeException("Error loading sim " + simId.toString(), e);
					}
			} catch (ToolkitRuntimeException e) { // Need to catch the exception here?
				logger.warn(ExceptionUtil.exception_details(e));
			}

		}
		return loadedSimIds;
	}
	
//*****************************************
//  These methods would normally belong in class SimulatorConfig but that
//  class is compiled for the client and some of these classes (ActorFactory)
//	do not belong on the client side.
//*****************************************
	static public Site getSite(SimulatorConfig config) throws Exception {
		AbstractActorFactory af = getActorFactory(config);
//        logger.info("Getting original actor factory to generate site - " + af.getClass().getName());
		return af.getActorSite(config, null);
	}

	static public AbstractActorFactory getActorFactory(SimulatorConfig config) throws Exception {
		String simtype = config.getActorType();
		ActorType at = ActorType.findActor(simtype);
		AbstractActorFactory af = AbstractActorFactory.getActorFactory(at);
		return af;
	}
//*****************************************


	
	public String sessionId() {
		return sessionId;
	}

	@Synchronized
	public void purge()  {
		List<SimulatorConfig> deletions = new ArrayList<>();
		for (SimulatorConfig sc : simConfigs) {
			if (SimDb.exists(sc.getId()))
				deletions.add(sc);
		}
		simConfigs.removeAll(deletions);
	}
	
	public void addSimConfigs(Simulator s) {
        logger.info("addSimConfigs: " + s);
		for (SimulatorConfig config : s.getConfigs()) {
			addSimConfig(config);
		}
	}

	public void addSimConfig(SimulatorConfig config) {
        logger.info("addSimConfig: " + config.getId());
		delSimConfig(config.getId());    // syncronized
		addSimConfigInternal(config);    // syncronized
	}

	@Synchronized
	private void addSimConfigInternal(SimulatorConfig config) {
		simConfigs.add(config);
	}

//	@Synchronized
//	public void setSimConfigs(List<SimulatorConfig> configs) {
//		logger.info("setSimConfigs: " + configs);
//        simConfigs = configs;
//	}

	@Synchronized
	private void delSimConfig(SimId simId) {
		int index = findSimConfig(simId);
		if (index != -1)
			simConfigs.remove(index);
	}


	private int findSimConfig(SimId simId) {
		for (int i=0; i<simConfigs.size(); i++) {
			if (simConfigs.get(i).getId().equals(simId)) {
				return i;
			}
		}
		return -1;
	}

	@Synchronized
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

	@Synchronized
	public Sites getAllSites(Sites commonSites)  throws Exception{
		Sites sites;

		loadAllSims();

		if (commonSites == null)
			sites = new Sites();
		else
			sites = commonSites.clone();

		for (SimulatorConfig asc : simConfigs) {
			if (!asc.isExpired()) {
				Site site = getSite(asc);
				if (site != null) // not all sims can generate a site
					sites.putSite(site);
			}
		}

		sites.buildRepositoriesSite();

		return sites;
	}

	public boolean exists(String siteName) {
		try {
			return getAllSites().exists(siteName);
		} catch (Exception e) {
			return false;
		}
	}

    public List<Site> getSites(List<String> siteNames) throws Exception {
        List<Site> siteList = new ArrayList<>();

        Collection<Site> sites = getAllSites().asCollection();
        for (Site site : sites) {
            if (siteNames.contains(site.getName()))
                siteList.add(site);
        }

        return siteList;
    }
	

	/**
	 * Return map from simName = simId
	 * @return
	 */
	@Synchronized
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
	 * Remove simulator config.  Managed as a list for convenience
	 * not because there can be multiple (there can't)
	 * @param simId
	 */
	@Synchronized
	 void removeSimulatorConfig(SimId simId) {
		List<SimulatorConfig> delete = new ArrayList<SimulatorConfig>();
		for (SimulatorConfig sc : simConfigs) {
			if (sc.getId().equals(simId))
				delete.add(sc);
		}
		simConfigs.removeAll(delete);
	}

	private boolean hasSim(SimId simId) {
		for (SimulatorConfig config : simConfigs) {
			if (config.getId() == null) continue;
			if (config.getId().equals(simId)) return true;
		}
		return false;
	}
}
