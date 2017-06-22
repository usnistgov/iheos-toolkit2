package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.sitemanagement.CombinedSiteLoader;
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.TransactionOfferingFactory;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Top level site management API referenced by calls from the GUI.  One
 * instance is shared between all sessions. This guarantees that
 * common sites are common. Calls to get session specific simulators
 * are managed through this class. Those calls are passed through to
 * a session specific cache managed by SimManager.
 * @author bill
 *
 */
public class SiteServiceManager {
	static SiteServiceManager siteServiceManager = null;
	Sites commonSites = null; // these are the common sites. The simulator based
	// sites are kept in Session.sites.

	static Logger logger = Logger.getLogger(SiteServiceManager.class);

	private SiteServiceManager() {
	}
	
	static public SiteServiceManager getSiteServiceManager() {
		if (siteServiceManager == null)	
			siteServiceManager = new SiteServiceManager();
		return siteServiceManager;
	}

	static public SiteServiceManager getInstance() { return getSiteServiceManager(); }

//	public void setCommonSites(Sites commonSites) {
//		this.commonSites = commonSites;
//	}
//
//	public Sites getSimSites(String sessionId) throws Exception {
//		return new SimCache().getSimManagerForSession(sessionId).getAllSites();
//	}

	public List<Site> getAllSites(String sessionId) throws Exception {
		logger.debug(sessionId + ": " + "getAllSites");
		ArrayList<Site> sites = new ArrayList<>();   //getCommonSites().asCollection());
		sites.addAll(new SimCache().getSimManagerForSession(sessionId).getAllSites().asCollection());
		List<String> names = new ArrayList<>();

		List<Site> sites2 = new ArrayList<>();
		for (Site s : sites) {
			if (s.getName().equals("allRepositories")) continue;
			sites2.add(s);
			names.add(s.getName());
		}
		logger.debug(names);
		return sites2;
	}

	public List<String> getSiteNamesWithRG(String sessionId) throws Exception {
		logger.debug(sessionId + ": " + "getSiteNamesWithRG");
		List<String> ss = new ArrayList<>();
		for (Site s : new SimCache().getSimManagerForSession(sessionId).getAllSites().asCollection()) {
			if (s.hasActor(ActorType.RESPONDING_GATEWAY))
				ss.add(s.getName());
		}
		return ss;
	}

	public List<String> getSiteNamesWithRepository(String sessionId) throws Exception {
		logger.debug(sessionId + ": " + "getSiteNamesWithRepository");
		List<String> ss = new ArrayList<>();
		for (Site s : new SimCache().getSimManagerForSession(sessionId).getAllSites().asCollection()) {
			if (s.hasActor(ActorType.REPOSITORY) || s.hasActor(ActorType.REPOSITORY_REGISTRY))
				ss.add(s.getName());
		}
		return ss;
	}

	public List<String> getSiteNamesWithRIG(String sessionId) throws Exception {
      logger.debug(sessionId + ": " + "getSiteNamesWithRIG");
      List<String> ss = new ArrayList<>();
      for (Site s : new SimCache().getSimManagerForSession(sessionId).getAllSites().asCollection()) {
         if (s.hasActor(ActorType.RESPONDING_IMAGING_GATEWAY))
            ss.add(s.getName());
      }
      return ss;
   }

   public List<String> getSiteNamesWithIDS(String sessionId) throws Exception {
      logger.debug(sessionId + ": " + "getSiteNamesWithIDS");
      List<String> ss = new ArrayList<>();
      for (Site s : new SimCache().getSimManagerForSession(sessionId).getAllSites().asCollection()) {
         if (s.hasActor(ActorType.IMAGING_DOC_SOURCE))
            ss.add(s.getName());
      }
      return ss;
   }

	public List<String> getSiteNamesByTran(String tranTypeStr, String sessionId) throws Exception {
		logger.debug(sessionId + ": " + "getSiteNamesWithRep");
		List<String> pnrSites = null;
		try {
			Collection<Site> siteCollection = new SimCache().getSimManagerForSession(sessionId).getAllSites().asCollection();

			Sites theSites = new Sites(siteCollection);

			TransactionType transactionType = TransactionType.find(tranTypeStr);

			if (transactionType!=null) {
				pnrSites = theSites.getSiteNamesWithTransaction(transactionType);
			} else {
				logger.error("transactionType is null.");
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return pnrSites;

	}
	
	public List<String> getSiteNames(String sessionId, boolean reload, boolean returnSimAlso)   {
		logger.debug(sessionId + ": " + "getSiteNames");

		if (reload)
			commonSites = null;

		try {
			if (returnSimAlso) {
				List<String> names = new ArrayList<>();
				for (Site s : getAllSites(sessionId))
					names.add(s.getName());
				return names;
			} else {
				List<String> names = new ArrayList<>();
				Sites sites = getCommonSites();
				for (Site s : sites.asCollection())
					names.add(s.getName());
				return names;
			}
		} catch (Exception e) {
			String msg = "Error collection site names for " + ((returnSimAlso) ? "common sites and simulators" : "common sites");
			logger.error(msg);
			return new ArrayList<>();
		}
	}

	// Statically defined sites (does not include simulators)
	public Sites getCommonSites() throws FactoryConfigurationError,
			Exception {
		if (commonSites == null) {
			if (!useActorsFile()) {
				File dir = Installation.instance().actorsDir();
				logger.debug("loading sites from " + dir);
				commonSites = new SeparateSiteLoader().load(dir, commonSites);
			} else {
				File loc = Installation.instance().propertyServiceManager().configuredActorsFile(true);
				if (loc == null)
					loc = Installation.instance().propertyServiceManager().internalActorsFile();
				logger.debug("loading sites from " + loc);
				commonSites = new CombinedSiteLoader().load(loc, commonSites);
			}
		}
		return commonSites;
	}

	// return common and simulator sites
	public Sites addSimulatorSites(String sessionId) throws Exception {
		try {
			return new SimCache().getSimManagerForSession(sessionId, true).getAllSites();
		} catch (Exception e) {
			logger.error("addSimulatorSites", e);
			throw new Exception(e.getMessage(), e);
		}
	}

	boolean useActorsFile() {
		if (useGazelleConfigFeed())
			return false;
		return Installation.instance().propertyServiceManager().getPropertyManager()
				.isUseActorsFile();
	}

	public boolean useGazelleConfigFeed() {
		String c = Installation.instance().propertyServiceManager().getPropertyManager()
				.getToolkitGazelleConfigURL();
		return c.trim().length() > 0;
	}

	public List<String> getRegistryNames(String sessionId) {
		logger.debug(sessionId + ": " + "getRegistryNames");
		try {
			getAllSites(sessionId);

			return new SimCache().getSimManagerForSession(sessionId).getAllSites().getSiteNamesWithActor(
					ActorType.REGISTRY);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

	public List<String> getUpdateNames(String sessionId) {
		logger.debug(sessionId + ": " + "getUpdateNames");
		try {
			getAllSites(sessionId);

			return new SimCache().getSimManagerForSession(sessionId).getAllSites().getSiteNamesWithTransaction(
					TransactionType.UPDATE);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

	public List<String> getRepositoryNames(String sessionId) {
		logger.debug(sessionId + ": " + "getRepositoryNames");
		try {
			getAllSites(sessionId);

			return new SimCache().getSimManagerForSession(sessionId).getAllSites().getSiteNamesWithRepository();
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

	public List<String> getRGNames(String sessionId) {
		logger.debug(sessionId + ": " + "getRGNames");
		try {
			getAllSites(sessionId);
			return new SimCache().getSimManagerForSession(sessionId)
					.getAllSites()
					.getSiteNamesWithActor(ActorType.RESPONDING_GATEWAY);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

	public List<String> getIGNames(String sessionId) {
		logger.debug(sessionId + ": " + "getIGNames");
		try {
			getAllSites(sessionId);
			return new SimCache().getSimManagerForSession(sessionId)
					.getAllSites()
					.getSiteNamesWithActor(ActorType.INITIATING_GATEWAY);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<String>();
		}

	}

	public List<String> reloadSites(String sessionId, boolean simAlso)
			throws FactoryConfigurationError, Exception {
		logger.debug(sessionId + ": " + "reloadSites");
		// sites = null;
		// loadSites();
		return getSiteNames(sessionId, true, simAlso);
	}

	public Site getSite(String sessionId, String siteName) throws Exception {
		logger.debug(sessionId + ": " + "getSite");
		try {
			getAllSites(sessionId);
			Site site = SimCache.getSimManagerForSession(sessionId).getAllSites().getSite(siteName);
			return site;
		} catch (Exception e) {
			logger.error("getSite", e);
			throw new Exception(e.getMessage());
		}
	}

	public String saveSite(String sessionId, Site site) throws Exception {
		logger.debug(sessionId + ": " + "saveSite");
		try {
            if (commonSites == null) commonSites = new Sites();
            commonSites.putSite(site);
			// sites.saveToFile(configuredActorsFile(false));
			if (!useActorsFile())
				new SeparateSiteLoader().saveToFile(Installation.instance()
						.actorsDir(), site);
			else {
				CombinedSiteLoader loader = new CombinedSiteLoader();
				loader.saveToFile(Installation.instance().propertyServiceManager()
						.configuredActorsFile(false), loader.toXML(commonSites));
			}
			addSimulatorSites(sessionId);
		} catch (Exception e) {
			logger.error("saveSite", e);
			throw new Exception(e.getMessage(), e);
		} catch (Throwable t) {
            logger.error(ExceptionUtil.exception_details(t));
        }
		return null;
	}

	public String deleteSite(String sessionId, String siteName) throws Exception {
		logger.debug(sessionId + ": " + "deleteSite");
		commonSites.deleteSite(siteName);
		try {
			// sites.saveToFile(configuredActorsFile(false));
			if (!useActorsFile())
				new SeparateSiteLoader().delete(Installation.instance()
						.actorsDir(), siteName);
			else
				new CombinedSiteLoader().saveToFile(Installation.instance().propertyServiceManager()
						.configuredActorsFile(false), commonSites);
			addSimulatorSites(sessionId);
		} catch (Exception e) {
			logger.error("deleteSite", e);
			throw new Exception(e.getMessage());
		}
		return null;
	}

	public Site getSite(Sites allSites, String name) throws Exception {
		return getSite(allSites.getAllSites().asCollection(), name);
	}

	public Site getSite(Collection<Site> allSites, String name) throws Exception {

		for (Site s : allSites) {
			if (s.getName().equals(name))
				return s;
		}

		throw new Exception("Site [" + name + "] is not defined");
	}

	// don't return sites implemented via simulators
	public List<String> reloadCommonSites() throws FactoryConfigurationError,
			Exception {
		logger.debug("reloadCommonSites");
		commonSites = null;
		commonSites = getCommonSites();
        if (commonSites == null) return new ArrayList<>();
		return commonSites.getSiteNames();
	}

	public TransactionOfferings getTransactionOfferings(String sessionId) throws Exception {
		logger.debug(sessionId + ": "
				+ "getTransactionOfferings");
		try {
			getAllSites(sessionId);
			Sites sits = new SimCache().getSimManagerForSession(sessionId).getAllSites();
			logger.debug("site Names: " + sits.getSiteNames());
			return new TransactionOfferingFactory(sits).get();
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			throw new Exception("getTransactionOfferings() failed", e);
		}
	}

	public List<String> getActorTypeNames(String sessionId) {
		logger.debug(sessionId + ": " + "getActorTypeNames");
		return ActorType.getActorNamesForConfigurationDisplays();
	}

}
