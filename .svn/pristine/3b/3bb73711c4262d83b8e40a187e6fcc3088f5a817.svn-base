package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.sitemanagement.CombinedSiteLoader;
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.TransactionOfferingFactory;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;

/**
 * One instance should be shared between all sessions. This guarantees that
 * common sites are common. Session is not kept as an instance variable
 * since this one instance is shared amongst all instances.
 * @author bill
 *
 */
public class SiteServiceManager extends CommonServiceManager {
	static SiteServiceManager siteServiceManager = null;
	public Sites sites = null; // these are the common sites. The simulator based
	// sites are kept in Session.sites.

	static Logger logger = Logger.getLogger(SiteServiceManager.class);

	private SiteServiceManager() {
	}
	
	static public SiteServiceManager getSiteServiceManager() {
		if (siteServiceManager == null)	
			siteServiceManager = new SiteServiceManager();
		return siteServiceManager;
	}

	public Sites getSites() {
		return sites;
	}

	public Collection<Site> getAllSites(String sessionId) throws Exception {
		logger.debug(sessionId + ": " + "getAllSites");
		List<Site> sits = new ArrayList<Site>();
		loadSites(sessionId);
		sits.addAll(sites.getAllSites().asCollection());
		return sits;
	}

	public List<String> getSiteNamesWithRG(String sessionId) throws Exception {
		logger.debug(sessionId + ": " + "getSiteNamesWithRG");
		List<String> ss = new ArrayList<String>();
		for (Site s : SimManager.getAllSites(sessionId, sites).asCollection()) {
			if (s.hasActor(ActorType.RESPONDING_GATEWAY))
				ss.add(s.getName());
		}
		return ss;
	}

	public List<String> getSiteNames(String sessionId, boolean reload, boolean simAlso) {
		logger.debug(sessionId + ": " + "getSiteNames");
		if (reload)
			sites = null;
		try {
			loadSites(sessionId);

			if (simAlso)
				return new ArrayList<String>(SimManager.getSites(sessionId).getSiteNames());
			return new ArrayList<String>(getSites().getSiteNames());
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<String>();
		}
	}

	public void loadSites(String sessionId) throws FactoryConfigurationError, Exception {
//		if (sites == null) {
			loadExternalSites(sessionId);
			addSimulatorSites(sessionId);
//		}
	}

	public Sites loadExternalSites(String sessionId) throws FactoryConfigurationError,
			Exception {
		if (sites == null) {
			if (!useActorsFile(sessionId)) {
				File dir = Installation.installation().propertyServiceManager().getActorsDirName();
				sites = new SeparateSiteLoader().load(dir, sites);
			} else {
				File loc = Installation.installation().propertyServiceManager().configuredActorsFile(true);
				if (loc == null)
					loc = Installation.installation().propertyServiceManager().internalActorsFile();
				sites = new CombinedSiteLoader().load(loc, sites);
			}
		}
		return sites;
	}

	// return common and simulator sites
	public Sites addSimulatorSites(String sessionId) throws Exception {
		try {
			return SimManager.getAllSites(sessionId, sites);
		} catch (Exception e) {
			logger.error("addSimulatorSites", e);
			throw new Exception(e.getMessage(), e);
		}
	}

	boolean useActorsFile(String sessionId) {
		if (useGazelleConfigFeed(sessionId))
			return false;
		String use = Installation.installation().propertyServiceManager().getPropertyManager()
				.getUseActorsFile();
		return "true".compareToIgnoreCase(use) == 0;
	}

	public boolean useGazelleConfigFeed(String sessionId) {
		String c = Installation.installation().propertyServiceManager().getPropertyManager()
				.getToolkitGazelleConfigURL();
		return c.trim().length() > 0;
	}

	public List<String> getRegistryNames(String sessionId) {
		logger.debug(sessionId + ": " + "getRegistryNames");
		try {
			loadSites(sessionId);

			return SimManager.getSites(sessionId).getSiteNamesWithActor(
					ActorType.REGISTRY);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<String>();
		}
	}

	public List<String> getUpdateNames(String sessionId) {
		logger.debug(sessionId + ": " + "getUpdateNames");
		try {
			loadSites(sessionId);

			return SimManager.getSites(sessionId).getSiteNamesWithTransaction(
					TransactionType.UPDATE);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<String>();
		}
	}

	public List<String> getRepositoryNames(String sessionId) {
		logger.debug(sessionId + ": " + "getRepositoryNames");
		try {
			loadSites(sessionId);

			return SimManager.getSites(sessionId).getSiteNamesWithRepository();
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<String>();
		}
	}

	public List<String> getRGNames(String sessionId) {
		logger.debug(sessionId + ": " + "getRGNames");
		try {
			loadSites(sessionId);
			return SimManager.getSites(sessionId).getSiteNamesWithActor(
					ActorType.RESPONDING_GATEWAY);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<String>();
		}
	}

	public List<String> getIGNames(String sessionId) {
		logger.debug(sessionId + ": " + "getIGNames");
		try {
			loadSites(sessionId);

			return SimManager.getSites(sessionId).getSiteNamesWithActor(
					ActorType.INITIATING_GATEWAY);
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
			loadSites(sessionId);
			return SimManager.getSites(sessionId).getSite(siteName);
		} catch (Exception e) {
			logger.error("getSite", e);
			throw new Exception(e.getMessage());
		}
	}

	public String saveSite(String sessionId, Site site) throws Exception {
		logger.debug(sessionId + ": " + "saveSite");
		sites.putSite(site);
		try {
			// sites.saveToFile(configuredActorsFile(false));
			if (!useActorsFile(sessionId))
				new SeparateSiteLoader().saveToFile(Installation.installation().propertyServiceManager()
						.getActorsDirName(), site);
			else {
				CombinedSiteLoader loader = new CombinedSiteLoader();
				loader.saveToFile(Installation.installation().propertyServiceManager()
						.configuredActorsFile(false), loader.toXML(sites));
			}
			addSimulatorSites(sessionId);
		} catch (Exception e) {
			logger.error("saveSite", e);
			throw new Exception(e.getMessage());
		}
		return null;
	}

	public String deleteSite(String sessionId, String siteName) throws Exception {
		logger.debug(sessionId + ": " + "deleteSite");
		sites.deleteSite(siteName);
		try {
			// sites.saveToFile(configuredActorsFile(false));
			if (!useActorsFile(sessionId))
				new SeparateSiteLoader().delete(Installation.installation().propertyServiceManager()
						.getActorsDirName(), siteName);
			else
				new CombinedSiteLoader().saveToFile(Installation.installation().propertyServiceManager()
						.configuredActorsFile(false), sites);
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

//	public Session setConfiguration(String sessionId, SiteSpec site) throws Exception {
//		if (site == null)
//			throw new Exception(
//					"Site not selected. May be caused by GUI session timeout");
//
//		try {
//			if (site.homeName == null)
//				site.homeName = site.name;
//			if (site.homeName != null)
//				site.homeId = SimManager.getSites(sessionId).getSite(site.homeName).getHome();
//		} catch (Exception e) {
//			throw new Exception("Cannot find homeCommunityId for site "
//					+ site.homeName);
//		}
//
//		session.siteSpec = site;
//		session.transactionSettings = new TransactionSettings();
//		session.isTls = site.isTls;
//		session.isSaml = site.isSaml;
//		session.transactionSettings.async = site.isAsync;
//		session.transactionSettings.issaml = site.isSaml;
//
//		if (session.repUid == null || session.repUid.equals("")) {
//			// this will not always work and is not always relevant - just try
//			try {
//				Site st = SimManager.getSites(session.id()).getSite(site.name);
//				session.repUid = st.getRepositoryUniqueId();
//			} catch (Exception e) {
//			}
//		}
//
//		return session;
//	}

	// don't return sites implemented via simulators
	public List<String> reloadExternalSites(String sessionId) throws FactoryConfigurationError,
			Exception {
		logger.debug(sessionId + ": " + "reloadExternalSites");
		sites = null;
		loadExternalSites(sessionId);
		return sites.getSiteNames();
	}

	public TransactionOfferings getTransactionOfferings(String sessionId) throws Exception {
		logger.debug(sessionId + ": "
				+ "getTransactionOfferings");
		try {
			loadSites(sessionId);
			Sites sits = SimManager.getAllSites(sessionId, sites);
			return new TransactionOfferingFactory(sits).get();
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			throw new Exception("getTransactionOfferings() failed", e);
		}
	}

	public List<String> getActorTypeNames(String sessionId) {
		logger.debug(sessionId + ": " + "getActorTypeNames");
		return ActorType.getActorNames();
	}

}
