package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.NoSimulatorException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
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
	static public Site getSite(SimulatorConfig config) throws ToolkitRuntimeException, NoSimulatorException {
		AbstractActorFactory af = getActorFactory(config);
//        logger.info("Getting original actor factory to generate site - " + af.getClass().getName());
		Site site = af.getActorSite(config, null);
		if (site == null) {
			String err = "Simulator " + config.getId() + "(type " + af.getClass().getName() + ") threw error when asked to generate site object";
			logger.error(err);
			throw new ToolkitRuntimeException(err);
		}
		return site.setSimulator(true);
	}

	static private AbstractActorFactory getActorFactory(SimulatorConfig config) {
		String simtype = config.getActorType();
		ActorType at = ActorType.findActor(simtype);
		AbstractActorFactory af = new GenericSimulatorFactory().getActorFactory(at);
		return af;
	}

	static public Site getSite(SimId simId) throws SimDoesNotExistException, ToolkitRuntimeException, NoSimulatorException {
		SimulatorConfig config = new SimDb().getSimulator(simId);
		if (config == null) {
			throw new SimDoesNotExistException("Simulator " + simId.toString() + " does not exist");
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
	static public Sites getAllSites(TestSession testSession) throws Exception {
		return getAllSites(SiteServiceManager.getSiteServiceManager().getCommonSites(testSession), testSession);
	}

	static public Sites getAllSites(Sites commonSites, TestSession testSession)  throws Exception {
		Sites sites;

		List<SimId> simIds = SimDb.getAllSimIds(testSession);

		if (commonSites == null)
			sites = new Sites(testSession);
		else
			sites = commonSites.clone();

		for (SimId simId : simIds) {
			try {
				Site site = getSite(simId);
				if (site != null)
					sites.putSite(site);
			} catch (NoSimulatorException nse) {
			} catch (SimDoesNotExistException sdnee) {
			}
		}

		sites.buildRepositoriesSite(testSession);

		return sites;
	}

	public boolean exists(String siteName, TestSession testSession) {
		try {
			if (siteName.equals("gov/nist/toolkit/installation/shared")) return true;
			if (SiteServiceManager.getSiteServiceManager().getCommonSites(testSession).exists(siteName)) return true;
			for (SimId simId : SimDb.getAllSimIds(testSession)) {
				if (siteName.equals(simId.toString())) return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

    public List<Site> getSites(List<String> siteNames, TestSession testSession) throws Exception {
        List<Site> siteList = new ArrayList<>();

        Collection<Site> sites = getAllSites(testSession).asCollection();
        for (Site site : sites) {
            if (siteNames.contains(site.getName()))
                siteList.add(site);
        }

        return siteList;
    }
}
