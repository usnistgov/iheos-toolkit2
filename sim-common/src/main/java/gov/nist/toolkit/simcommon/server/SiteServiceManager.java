package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.TransactionOfferingFactory;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.util.*;

import static gov.nist.toolkit.simcommon.server.AbstractActorFactory.getActorFactory;

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
	private static SiteServiceManager siteServiceManager = null;
	private Map<TestSession, Sites> commonSites = new HashMap<>(); // these are the common sites. The simulator based
	// sites are kept in Session.sites.
	private boolean alwaysReload = true;

	static Logger logger = Logger.getLogger(SiteServiceManager.class);

	private SiteServiceManager() {
	}
	
	static public SiteServiceManager getSiteServiceManager() {
		if (siteServiceManager == null)	
			siteServiceManager = new SiteServiceManager();
		return siteServiceManager;
	}

	static public SiteServiceManager getInstance() { return getSiteServiceManager(); }

	// includes sims too
	public List<Site> getAllSites(String sessionId, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "getAllSites");

		Map<String, Site> sites = new HashMap<>();

		for (Site site : getCommonSites(TestSession.DEFAULT_TEST_SESSION).asCollection())
			sites.put(site.getName(), site);
		for (Site site : getCommonSites(testSession).asCollection())
			sites.put(site.getName(), site);

		List<SimId> simIds;
		simIds = SimDb.getAllSimIds(TestSession.DEFAULT_TEST_SESSION);
		for (SimId simId : simIds) {
			SimulatorConfig config = new SimDb().getSimulator(simId);
			AbstractActorFactory af = getActorFactory(config);
			Site site = af.getActorSite(config, null);
			sites.put(site.getName(), site);
		}
		simIds = SimDb.getAllSimIds(testSession);
		for (SimId simId : simIds) {
			SimulatorConfig config = new SimDb().getSimulator(simId);
			AbstractActorFactory af = getActorFactory(config);
			Site site = af.getActorSite(config, null);
			sites.put(site.getName(), site);
		}

		List<Site> returns = new ArrayList<>();
		returns.addAll(sites.values());
		return returns;

	}


	public List<String> getSiteNamesWithRG(String sessionId, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "getSiteNamesWithRG");
		List<String> ss = new ArrayList<>();
		for (Site s : SimManager.getAllSites(testSession).asCollection()) {
			if (s.hasActor(ActorType.RESPONDING_GATEWAY))
				ss.add(s.getName());
		}
		return ss;
	}

	public List<String> getSiteNamesWithRepository(String sessionId, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "getSiteNamesWithRepository");
		List<String> ss = new ArrayList<>();
		for (Site s : SimManager.getAllSites(testSession).asCollection()) {
			if (s.hasActor(ActorType.REPOSITORY) || s.hasActor(ActorType.REPOSITORY_REGISTRY))
				ss.add(s.getName());
		}
		return ss;
	}

	public List<String> getSiteNamesWithRIG(String sessionId, TestSession testSession) throws Exception {
      logger.debug(sessionId + ": " + "getSiteNamesWithRIG");
      List<String> ss = new ArrayList<>();
      for (Site s : SimManager.getAllSites(testSession).asCollection()) {
         if (s.hasActor(ActorType.RESPONDING_IMAGING_GATEWAY))
            ss.add(s.getName());
      }
      return ss;
   }

   public List<String> getSiteNamesWithIDS(String sessionId, TestSession testSession) throws Exception {
      logger.debug(sessionId + ": " + "getSiteNamesWithIDS");
      List<String> ss = new ArrayList<>();
      for (Site s : SimManager.getAllSites(testSession).asCollection()) {
         if (s.hasActor(ActorType.IMAGING_DOC_SOURCE))
            ss.add(s.getName());
      }
      return ss;
   }

	public List<String> getSiteNamesByTran(String tranTypeStr, String sessionId, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "getSiteNamesWithRep");
		List<String> pnrSites = null;
		try {
			Collection<Site> siteCollection = SimManager.getAllSites(testSession).asCollection();

			Sites theSites = new Sites(siteCollection);

			TransactionType transactionType = TransactionType.find(tranTypeStr);

			if (transactionType!=null) {
				pnrSites = theSites.getSiteNamesWithTransaction(transactionType, testSession);
			} else {
				logger.error("transactionType is null.");
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return pnrSites;

	}

	public List<String> getSiteNames(String sessionId, boolean reload, boolean returnSimAlso, TestSession testSession)   {
		logger.debug(sessionId + ": " + "getSiteNames");

		try {
			if (returnSimAlso) {  // implemented as return sim ONLY???
				List<String> names = new ArrayList<>();
				for (Site s : getAllSites(sessionId, testSession))
					names.add(s.getName());
				return names;
			} else {
				Set<String> names = new HashSet<>();
				Sites sites = getCommonSites(testSession);
				if (sites != null) {
					for (Site s : sites.asCollection())
						names.add(s.getName());
				}
				if (!testSession.equals(TestSession.DEFAULT_TEST_SESSION)) {
					sites = getCommonSites(TestSession.DEFAULT_TEST_SESSION);
					if (sites != null) {
						for (Site s : sites.asCollection())
							names.add(s.getName());
					}
				}
				List<String> nameList = new ArrayList<>();
				nameList.addAll(names);
				return nameList;
			}
		} catch (Exception e) {
			String msg = "Error collection site names for " + ((returnSimAlso) ? "common sites and simulators" : "common sites");
			logger.error(msg);
			logger.error(ExceptionUtil.exception_details(e));
			return new ArrayList<>();
		}
	}

	private void load(TestSession testSession) throws Exception {
		File dir = Installation.instance().actorsDir(testSession);
		logger.debug("loading sites from " + dir);
		commonSites.put(testSession, new SeparateSiteLoader(testSession).load(dir, null));
	}

	// Statically defined sites (does not include simulators)
	// how is the commonSites variable helping?
	public Sites getCommonSites(TestSession testSession) throws FactoryConfigurationError,
			Exception {

		load(TestSession.DEFAULT_TEST_SESSION);
		if (!testSession.equals(TestSession.DEFAULT_TEST_SESSION))
			load(testSession);

//		if (testSession.equals(TestSession.DEFAULT_TEST_SESSION))
//			commonSites.put(testSession, new Sites(testSession));  // clear out old
//		else
//			getCommonSites(TestSession.DEFAULT_TEST_SESSION);  // load up default sites
//
//		if (!useActorsFile()) {
//			File dir = Installation.instance().actorsDir(testSession);
//			logger.debug("loading sites from " + dir);
//			commonSites.put(testSession, new SeparateSiteLoader(testSession).load(dir, commonSites.get(testSession)));
//		} else {
//			throw new ToolkitRuntimeException("Combined site (all in one file) no longer supported");
//		}
//
//
//		if (commonSites.get(TestSession.DEFAULT_TEST_SESSION) == null)
//			commonSites.put(TestSession.DEFAULT_TEST_SESSION, new Sites(TestSession.DEFAULT_TEST_SESSION));
//		if (commonSites.get(testSession) == null)
//			commonSites.put(testSession, new Sites(testSession));
//

		if (testSession.equals(TestSession.DEFAULT_TEST_SESSION))
			return commonSites.get(TestSession.DEFAULT_TEST_SESSION);

		Sites sites = commonSites.get(testSession).add(commonSites.get(TestSession.DEFAULT_TEST_SESSION));
		return sites;

//		if (commonSites.get(testSession) == null)
//			return new Sites(testSession);
//		return commonSites.get(testSession);
	}

	private boolean useActorsFile() {
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

	public List<String> getRegistryNames(String sessionId, TestSession testSession) {
		logger.debug(sessionId + ": " + "getRegistryNames");
		try {
			getAllSites(sessionId, testSession);

			return SimManager.getAllSites(testSession).getSiteNamesWithActor(
					ActorType.REGISTRY, testSession);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

	public List<String> getUpdateNames(String sessionId, TestSession testSession) {
		logger.debug(sessionId + ": " + "getUpdateNames");
		try {
			getAllSites(sessionId, testSession);

			return SimManager.getAllSites(testSession).getSiteNamesWithTransaction(
					TransactionType.UPDATE, testSession);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

	public List<String> getRepositoryNames(String sessionId, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "getRepositoryNames");
			return asSites(getAllSites(sessionId, testSession)).getSiteNamesWithRepository(testSession);
	}

	private TestSession getTestSession(List<Site> sites) {
		for (Site s : sites) {
			if (!s.getTestSession().equals(TestSession.DEFAULT_TEST_SESSION))
				return s.getTestSession();
		}
		return TestSession.DEFAULT_TEST_SESSION;
	}

	private Sites asSites(List<Site> theSites) {
		TestSession testSession = getTestSession(theSites);
		Sites sites = new Sites(testSession);

		for (Site site : theSites) {
			sites.add(site);
		}

		return sites;
	}

	public List<String> getRGNames(String sessionId, TestSession testSession) {
		logger.debug(sessionId + ": " + "getRGNames");
		try {
			getAllSites(sessionId, testSession);
			return SimManager
					.getAllSites(testSession)
					.getSiteNamesWithActor(ActorType.RESPONDING_GATEWAY, testSession);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

	public List<String> getIGNames(String sessionId, TestSession testSession) {
		logger.debug(sessionId + ": " + "getIGNames");
		try {
			getAllSites(sessionId, testSession);
			return SimManager
					.getAllSites(testSession)
					.getSiteNamesWithActor(ActorType.INITIATING_GATEWAY, testSession);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<String>();
		}

	}

	public List<String> reloadSites(String sessionId, boolean simAlso, TestSession testSession)
			throws FactoryConfigurationError, Exception {
		logger.debug(sessionId + ": " + "reloadSites");
		return getSiteNames(sessionId, true, simAlso, testSession);
	}


	public Site getSite(String sessionId, String siteName, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "getSite");
		try {
			try {
				return SimManager.getAllSites(testSession).getSite(siteName, testSession);
			} catch (Exception e) {
				return SimManager.getAllSites(TestSession.DEFAULT_TEST_SESSION).getSite(siteName, TestSession.DEFAULT_TEST_SESSION);
			}
		} catch (Exception e) {
			logger.error("getSite", e);
			throw new Exception(e.getMessage());
		}
	}


	public String saveSite(String sessionId, Site site, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "saveSite");
		try {

			if (commonSites.get(testSession) == null) commonSites.put(testSession, new Sites(testSession));
			commonSites.get(testSession).putSite(site);
			// sites.saveToFile(configuredActorsFile(false));
			if (!useActorsFile())
				new SeparateSiteLoader(testSession).saveToFile(Installation.instance()
						.actorsDir(testSession), site);
			else {
				throw new ToolkitRuntimeException("Combined site (all in one file) no longer supported");
			}
		} catch (Exception e) {
			logger.error("saveSite", e);
			throw new Exception(e.getMessage(), e);
		} catch (Throwable t) {
			logger.error(ExceptionUtil.exception_details(t));
		}
		return null;
	}

	public String deleteSite(String sessionId, String siteName, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": " + "deleteSite");
		if (commonSites.get(testSession) != null)
			commonSites.get(testSession).deleteSite(siteName);
		try {
			// sites.saveToFile(configuredActorsFile(false));
			if (!useActorsFile())
				new SeparateSiteLoader(testSession).delete(Installation.instance()
						.actorsDir(testSession), siteName);
			else
				throw new ToolkitRuntimeException("Combined site (all in one file) no longer supported");
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
	public List<String> reloadCommonSites(TestSession testSession) throws FactoryConfigurationError,
			Exception {
		logger.debug("reloadCommonSites");
		getCommonSites(testSession);    // does reload
        if (commonSites.get(testSession) == null) return new ArrayList<>();
		return commonSites.get(testSession).getSiteNames();
	}

	public TransactionOfferings getTransactionOfferings(String sessionId, TestSession testSession) throws Exception {
		logger.debug(sessionId + ": "
				+ "getTransactionOfferings");
		try {
			getAllSites(sessionId, testSession);
			Sites sits = SimManager.getAllSites(testSession);
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
