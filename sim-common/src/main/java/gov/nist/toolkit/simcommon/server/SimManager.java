package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Maintains the list of loaded SimulatorConfig objects for a
 * single session. This is obsolete
 *
 * Mostly this is a few static functions and a reference to the sessionId
 *
 * All methods that reference simConfigs are labeled @Synchronized
 * @author bill
 *
 */

public class SimManager {
	private String sessionId;  // this is never used internally.  Other classes use it through the getter.
	private static Logger logger = Logger.getLogger(SimManager.class);


	public SimManager(String sessionId) {
		this.sessionId = sessionId;
	}

//*****************************************
//  These methods would normally belong in class SimulatorConfig but that
//  class is compiled for the client and some of these classes (ActorFactory)
//	do not belong on the client side.
//*****************************************
	static public Site getSite(SimulatorConfig config) throws Exception {
		AbstractActorFactory af = getActorFactory(config);
//        logger.info("Getting original actor factory to generate site - " + af.getClass().getName());
		Site site = af.getActorSite(config, null);
		if (site == null) {
			String err = "Simulator " + config.getId() + "(type " + af.getClass().getName() + ") threw error when asked to generate site object";
			logger.error(err);
			throw new Exception(err);
		}
		return site.setSimulator(true);
	}

	static private AbstractActorFactory getActorFactory(SimulatorConfig config) throws Exception {
		String simtype = config.getActorType();
		ActorType at = ActorType.findActor(simtype);
		AbstractActorFactory af = new GenericSimulatorFactory().getActorFactory(at);
		return af;
	}

	static public Site getSite(SimId simId) throws Exception {
		SimulatorConfig config = new SimDb().getSimulator(simId);
		if (config == null) {
			throw new Exception("Simulator " + simId.toString() + " does not exist");
		}
		return getSite(config);
	}

//*****************************************
	
	public String sessionId() {
		return sessionId;
	}

	/**
	 * Get common sites and sim sites defined for this session.
	 * @return
	 * @throws Exception
	 */
	static public Sites getAllSites() throws Exception {
		return getAllSites(SiteServiceManager.getSiteServiceManager().getCommonSites());
	}

	static public Sites getAllSites(Sites commonSites)  throws Exception {
		Sites sites;

		List<SimId> simIds = SimDb.getAllSimIds();

		if (commonSites == null)
			sites = new Sites();
		else
			sites = commonSites.clone();

		for (SimId simId : simIds) {
			Site site = getSite(simId);
			if (site != null)
				sites.putSite(site);
		}

		sites.buildRepositoriesSite();

		return sites;
	}

	public boolean exists(String siteName) {
		try {
			if (siteName.equals("client")) return true;
			if (SiteServiceManager.getSiteServiceManager().getCommonSites().exists(siteName)) return true;
			for (SimId simId : SimDb.getAllSimIds()) {
				if (siteName.equals(simId.toString())) return true;
			}
			return false;
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
}
