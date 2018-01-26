package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.SimCache;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.CombinedSiteLoader;
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
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

	private static Logger logger = Logger.getLogger(SiteServiceManager.class);

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

//	public List<String> getSiteNamesWithRG(String sessionId, TestSession testSession) throws Exception {
//		logger.debug(sessionId + ": " + "getSiteNamesWithRG");
//		List<String> ss = new ArrayList<>();
//		for (Site s : SimManager.getAllSites(testSession).asCollection()) {
//			if (s.hasActor(ActorType.RESPONDING_GATEWAY))
//				ss.add(s.getName());
//		}
//		return ss;
//	}

//	public List<String> getSiteNamesWithRepository(String sessionId, TestSession testSession) throws Exception {
//		logger.debug(sessionId + ": " + "getSiteNamesWithRepository");
//		List<String> ss = new ArrayList<>();
//		for (Site s : SimManager.getAllSites(testSession).asCollection()) {
//			if (s.hasActor(ActorType.REPOSITORY) || s.hasActor(ActorType.REPOSITORY_REGISTRY))
//				ss.add(s.getName());
//		}
//		return ss;
//	}

//	public List<String> getSiteNamesWithRIG(String sessionId, TestSession testSession) throws Exception {
//      logger.debug(sessionId + ": " + "getSiteNamesWithRIG");
//      List<String> ss = new ArrayList<>();
//      for (Site s : SimManager.getAllSites(testSession).asCollection()) {
//         if (s.hasActor(ActorType.RESPONDING_IMAGING_GATEWAY))
//            ss.add(s.getName());
//      }
//      return ss;
//   }

//   public List<String> getSiteNamesWithIDS(String sessionId, TestSession testSession) throws Exception {
//      logger.debug(sessionId + ": " + "getSiteNamesWithIDS");
//      List<String> ss = new ArrayList<>();
//      for (Site s : SimManager.getAllSites(testSession).asCollection()) {
//         if (s.hasActor(ActorType.IMAGING_DOC_SOURCE))
//            ss.add(s.getName());
//      }
//      return ss;
//   }

//	public List<String> getSiteNamesByTran(String tranTypeStr, String sessionId, TestSession testSession) throws Exception {
//		logger.debug(sessionId + ": " + "getSiteNamesWithRep");
//		List<String> pnrSites = null;
//		try {
//			Collection<Site> siteCollection = SimManager.getAllSites(testSession).asCollection();
//
//			Sites theSites = new Sites(siteCollection);
//
//			TransactionType transactionType = TransactionType.find(tranTypeStr);
//
//			if (transactionType!=null) {
//				pnrSites = theSites.getSiteNamesWithTransaction(transactionType, testSession);
//			} else {
//				logger.error("transactionType is null.");
//			}
//
//		} catch (Exception e) {
//			logger.error(e.toString());
//		}
//		return pnrSites;
//
//	}
	
	public List<String> getSiteNames(String sessionId, boolean reload, boolean returnSimAlso, TestSession testSession)   {
		logger.debug(sessionId + ": " + "getSiteNames");

//		if (reload)
			commonSites = null;  // force reload

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

	// Statically defined sites (does not include simulators)
	private Sites getCommonSites(TestSession testSession) throws FactoryConfigurationError,
			Exception {
		commonSites = null;
		if (commonSites == null) {
			if (testSession.equals(TestSession.DEFAULT_TEST_SESSION))
				commonSites = new HashMap<>();
			else
				getCommonSites(TestSession.DEFAULT_TEST_SESSION);

			if (!useActorsFile()) {
				File dir = Installation.instance().actorsDir(testSession);
				logger.debug("loading sites from " + dir);
				commonSites.put(testSession, new SeparateSiteLoader(testSession).load(dir, commonSites.get(testSession)));
			} else {
				throw new ToolkitRuntimeException("Combined site (all in one file) no longer supported");
			}
		}
		return commonSites.get(testSession);
	}

	// return common and simulator sites
	private Sites addSimulatorSites(String sessionId, TestSession testSession) throws Exception {
		try {
			return new SimCache().getSimManagerForSession(sessionId, true).getAllSites(testSession);
		} catch (Exception e) {
			logger.error("addSimulatorSites", e);
			throw new Exception(e.getMessage(), e);
		}
	}

	private boolean useActorsFile() {
		if (useGazelleConfigFeed())
			return false;
		return Installation.instance().propertyServiceManager().getPropertyManager()
				.isUseActorsFile();
	}

	private boolean useGazelleConfigFeed() {
		String c = Installation.instance().propertyServiceManager().getPropertyManager()
				.getToolkitGazelleConfigURL();
		return c.trim().length() > 0;
	}

//	public List<String> getRegistryNames(String sessionId, TestSession testSession) {
//		logger.debug(sessionId + ": " + "getRegistryNames");
//		try {
//			getAllSites(sessionId, testSession);
//
//			return new SimCache().getSimManagerForSession(sessionId).getAllSites(testSession).getSiteNamesWithActor(
//					ActorType.REGISTRY, testSession);
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e, 10));
//			return new ArrayList<>();
//		}
//	}

//	public List<String> getUpdateNames(String sessionId, TestSession testSession) {
//		logger.debug(sessionId + ": " + "getUpdateNames");
//		try {
//			getAllSites(sessionId, testSession);
//
//			return new SimCache().getSimManagerForSession(sessionId).getAllSites(testSession).getSiteNamesWithTransaction(
//					TransactionType.UPDATE, testSession);
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e, 10));
//			return new ArrayList<>();
//		}
//	}

	public List<String> getRepositoryNames(String sessionId, TestSession testSession) {
		logger.debug(sessionId + ": " + "getRepositoryNames");
		try {
//			getAllSites(sessionId, testSession);  //????????

			Set<String> names = new HashSet<>();

			names.addAll(SimManager.getAllSites(testSession).getSiteNamesWithRepository(testSession));
			if (!testSession.equals((TestSession.DEFAULT_TEST_SESSION))) {
				List<String> names2 = SimManager.getAllSites(TestSession.DEFAULT_TEST_SESSION).getSiteNamesWithRepository(TestSession.DEFAULT_TEST_SESSION);
				names.addAll(names2);
			}
			List<String> returns = new ArrayList<>();
			returns.addAll(names);
			return returns;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e, 10));
			return new ArrayList<>();
		}
	}

//	public List<String> getRGNames(String sessionId, TestSession testSession) {
//		logger.debug(sessionId + ": " + "getRGNames");
//		try {
//			getAllSites(sessionId, testSession);
//			return new SimCache().getSimManagerForSession(sessionId)
//					.getAllSites(testSession)
//					.getSiteNamesWithActor(ActorType.RESPONDING_GATEWAY, testSession);
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e, 10));
//			return new ArrayList<>();
//		}
//	}

//	public List<String> getIGNames(String sessionId, TestSession testSession) {
//		logger.debug(sessionId + ": " + "getIGNames");
//		try {
//			getAllSites(sessionId, testSession);
//			return new SimCache().getSimManagerForSession(sessionId)
//					.getAllSites(testSession)
//					.getSiteNamesWithActor(ActorType.INITIATING_GATEWAY, testSession);
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e, 10));
//			return new ArrayList<String>();
//		}
//
//	}

	public List<String> reloadSites(String sessionId, boolean simAlso, TestSession testSession)
			throws FactoryConfigurationError, Exception {
		logger.debug(sessionId + ": " + "reloadSites");
		// sites = null;
		// loadSites();
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
				CombinedSiteLoader loader = new CombinedSiteLoader(testSession);
				loader.saveToFile(Installation.instance().propertyServiceManager()
						.configuredActorsFile(false), loader.toXML(commonSites.get(testSession)));
			}
			addSimulatorSites(sessionId, testSession);
		} catch (Exception e) {
			logger.error("saveSite", e);
			throw new Exception(e.getMessage(), e);
		} catch (Throwable t) {
            logger.error(ExceptionUtil.exception_details(t));
        }
		return null;
	}

//	public String deleteSite(String sessionId, String siteName, TestSession testSession) throws Exception {
//		logger.debug(sessionId + ": " + "deleteSite");
//		commonSites.get(testSession).deleteSite(siteName);
//		try {
//			// sites.saveToFile(configuredActorsFile(false));
//			if (!useActorsFile())
//				new SeparateSiteLoader(testSession).delete(Installation.instance()
//						.actorsDir(testSession), siteName);
//			else
//				new CombinedSiteLoader(testSession).saveToFile(Installation.instance().propertyServiceManager()
//						.configuredActorsFile(false), commonSites.get(testSession));
//			addSimulatorSites(sessionId, testSession);
//		} catch (Exception e) {
//			logger.error("deleteSite", e);
//			throw new Exception(e.getMessage());
//		}
//		return null;
//	}

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
//	public List<String> reloadCommonSites(TestSession testSession) throws FactoryConfigurationError,
//			Exception {
//		logger.debug("reloadCommonSites");
//		commonSites = null;  // force reload
//		commonSites.put(testSession, getCommonSites(testSession));
//        if (commonSites == null) return new ArrayList<>();
//		return commonSites.get(testSession).getSiteNames();
//	}

//	public TransactionOfferings getTransactionOfferings(String sessionId, TestSession testSession) throws Exception {
//		logger.debug(sessionId + ": "
//				+ "getTransactionOfferings");
//		try {
//			getAllSites(sessionId, testSession);
//			Sites sits = new SimCache().getSimManagerForSession(sessionId).getAllSites(testSession);
//			logger.debug("site Names: " + sits.getSiteNames());
//			return new TransactionOfferingFactory(sits).get();
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e));
//			throw new Exception("getTransactionOfferings() failed", e);
//		}
//	}

//	public List<String> getActorTypeNames(String sessionId) {
//		logger.debug(sessionId + ": " + "getActorTypeNames");
//		return ActorType.getActorNamesForConfigurationDisplays();
//	}

}
