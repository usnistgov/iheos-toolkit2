package gov.nist.toolkit.session.server;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SimulatorFactory;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyServiceManager;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.session.server.serviceManager.QueryServiceManager;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.simcommon.server.ExtendedPropertyManager;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.TransactionSettings;
import gov.nist.toolkit.testengine.Xdstest2;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The session object is used in one of four ways depending on the context:
 * 
 * 1) GUI - each GUI session is represented by a session object. It is managed
 * through the Servlet session mechanism. It is managed by the class
 * ToolkitServiceImpl. This is a single threaded use of the session instance.
 * 
 * 2) Simulators - on session object is used and shared by all simulator
 * instances. It is managed by the class SimServlet. This is a multi-threaded
 * use of the session instance.
 * 
 * 3) ValServlet - RESTful service for access to validators. Just like
 * simulators above.
 * 
 * 4) Dashboard - coming soon.
 * 
 * @author bill
 * 
 */
public class Session implements SecurityParams {

	public Xdstest2 xt;
	public SiteSpec siteSpec = new SiteSpec();
	public String repUid;
	// public boolean isTls;
	// public boolean isSaml;
	public AssertionResults res;
	public TransactionSettings transactionSettings = new TransactionSettings();
	public boolean isAdmin = false;
	public boolean isSoap = true;

	byte[] lastUpload = null;
	byte[] input2 = null;
	String lastUploadFilename = null;
	String input2Filename = null;
	byte[] lastUpload2 = null;
	String lastUploadFilename2 = null;
	String password1 = null;
	String password2 = null;

	// File tomcatSessionCache = null; // one for this Tomcat session
	// (corresponds to this Session object)
	File mesaSessionCache = null; // changes each time new mesaSessionName
									// changes

	Metadata lastMetadata = null;
	public String ipAddr = null; // also used as default sim db id
	String serverIP = null;
	String serverPort = null;
	// List<SimulatorConfig> actorSimulatorConfigs = new
	// ArrayList<SimulatorConfig>();
	SimCache simCache = new SimCache();
	String sessionId;

	// File warHome = null;
	// File externalCache = null;
	File toolkit = null;

	// File currentEnvironmentDir = null;
	String currentEnvironmentName = null;

	String mesaSessionName = null;
	SessionPropertyManager sessionProperties = null;
	PropertyServiceManager propertyServiceMgr = null;
	XdsTestServiceManager xdsTestServiceManager = null;
	QueryServiceManager queryServiceMgr = null;
	static Map<String, Session> sessionMap = new HashMap<String, Session>();
	static final Logger logger = Logger.getLogger(Session.class);

	public boolean isTls() {
		return siteSpec.isTls;
	}

	public boolean isSaml() {
		return siteSpec.isSaml;
	}

	public boolean isAsync() {
		return siteSpec.isAsync;
	}

	public void setTls(boolean tls) {
		siteSpec.isTls = tls;
	}

	public void setSaml(boolean saml) {
		siteSpec.isSaml = saml;
	}

	public void setAsync(boolean async) {
		siteSpec.isAsync = async;
	}

	public void verifyCurrentEnvironment() throws EnvironmentNotSelectedException {
		EnvSetting.getEnvSetting(sessionId);
	}

	// // not to be trusted. Use SimulatorServiceManager instead
	// public List<SimulatorConfig> getSimConfigs() {
	// return actorSimulatorConfigs;
	// }
	//
	public void addSession() {
		sessionMap.put(sessionId, this);
	}

	static public Session getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	public void setSessionId(String id) {
		sessionId = id;
	}

	// undefined - no possible answer
	// @Deprecated
	// public String getHome() throws Exception {
	// logger.debug(getId() + ": " + "getHome");
	// Site s2 = SimManager.getSites(getId()).getSite(siteSpec.name);
	// return s2.getHome();
	// }

	public void setSiteSpec(SiteSpec siteSpec) {
		this.siteSpec = siteSpec;
		transactionSettings = new TransactionSettings();
		transactionSettings.siteSpec = siteSpec;

		if (repUid == null || repUid.equals("")) {
			// this will not always work and is not always relevant - just try
			// WHY?
			try {
				Sites sites = new SimCache().getSimManagerForSession(id()).getAllSites();
				Site st = sites.getSite(siteSpec.name);
				repUid = st.getRepositoryUniqueId();
			} catch (Exception e) {
			}
		}

	}

	public File getTomcatSessionCache() {
		return new File(Installation.installation().warHome() + File.separator + "SessionCache" + File.separator
				+ sessionId);
	}

	public File getMesaSessionCache() {
		return mesaSessionCache;
	}

	public String getServerIP() {
		return serverIP;
	}

	public String getServerPort() {
		return serverPort;
	}

	public Session(File warHome, SiteServiceManager siteServiceManager, String sessionId) {
		this(warHome, siteServiceManager);
		this.sessionId = sessionId;

		// tomcatSessionCache = new File(warHome + File.separator +
		// "SessionCache" + File.separator + sessionId);
	}

	public Session(File warHome, SiteServiceManager siteServiceManager) {
		Installation.installation().warHome(warHome);
		// this.siteServiceManager = siteServiceManager;
		ExtendedPropertyManager.load(warHome);
		System.out.print("warHome[Session]: " + warHome + "\n");

		File externalCache = new File(Installation.installation().propertyServiceManager().getPropertyManager()
				.getExternalCache());
		Installation.installation().externalCache(externalCache);
		if (externalCache == null || !externalCache.exists() || !externalCache.isDirectory())
			externalCache = null;
		Installation.installation().externalCache(externalCache);
	}

	public QueryServiceManager queryServiceManager() {
		if (queryServiceMgr == null)
			queryServiceMgr = new QueryServiceManager(this);
		return queryServiceMgr;
	}

	public XdsTestServiceManager xdsTestServiceManager() {
		if (xdsTestServiceManager == null)
			xdsTestServiceManager = new XdsTestServiceManager(this);
		return xdsTestServiceManager;
	}

	// public SiteServiceManager siteServiceManager() {
	// return siteServiceManager;
	// }

	public void setMesaSessionName(String name) {
		mesaSessionName = name;

		File testLogCache;
		try {
			testLogCache = Installation.installation().propertyServiceManager().getTestLogCache();
		} catch (Exception e) {
			return;
		}

		mesaSessionCache = new File(testLogCache + File.separator + mesaSessionName);
		mesaSessionCache.mkdirs();
	}

	boolean mesaSessionExists() {
		return mesaSessionCache != null;
	}

	public String getMesaSessionName() {
		return mesaSessionName;
	}

	void setSessionProperty(String name, String value) {
		SessionPropertyManager props = getSessionProperties();
		if (props == null)
			return;
		props.set(name, value);
		props.save();
	}

	public void setSessionProperties(Map<String, String> m) {
		SessionPropertyManager props = getSessionProperties();
		if (props == null)
			return;
		props.clear();
		props.add(m);
		props.save();
	}

	/**
	 * Get id of current Session
	 * 
	 * @return
	 */
	public String getId() {
		return sessionId;
	}

	public String id() {
		return sessionId;
	}

	// public Sites loadActorSimulatorConfigs(Sites commonSites, List<String>
	// ids) throws Exception {
	// actorSimulatorConfigs = new
	// SimulatorFactory(SimManager.getSimManagerForSession(sessionId)).loadSimulators(ids);
	// SimManager.getSimManagerForSession(sessionId).setSimConfigs(actorSimulatorConfigs);
	// return SimManager.getAllSites(sessionId, commonSites);
	// }

	// /**
	// *
	// * @return map from simulator name (private name) to simulator id (global
	// id)
	// */
	// public Map<String, String> getActorSimulatorNameMap() {
	// Map<String, String> nameMap = new HashMap<String, String>();
	//
	// for (SimulatorConfig sc : actorSimulatorConfigs) {
	// String name = sc.getDefaultName();
	// String id = sc.getId();
	// nameMap.put(name, id);
	// }
	//
	// return nameMap;
	// }

	// public List<String> reloadSites(Sites commonSites) throws Exception {
	// sites = getAllSites(commonSites);
	// List<String> names = sites.getSiteNames();
	// logger.debug("reloadSites: " + names);
	// return names;
	// }

	public String getSimBaseEndpoint() {
		// the last piece must agree with simulatorServlet in web.xml
		return "http://" + serverIP + ":" + serverPort + "/xdstools2/simulator";
	}

	public void setServerSpec(String ip, String port) {
		serverIP = translateIPAddr(ip);
		serverPort = port;

	}

	String translateIPAddr(String ip) {
		if ("0:0:0:0:0:0:0:1%0".equals(ip)) {
			// value returned when in GWT hosted mode
			return "127.0.0.1";
		}
		return ip;

	}

	public void setIpAddr(String ip) {
		ipAddr = translateIPAddr(ip);
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public String getDefaultSimId() {
		return ipAddr;
	}

	public void setLastUpload(String filename, byte[] last, String filename2, byte[] last2) {
		lastUploadFilename = filename;
		lastUpload = last;

		lastUploadFilename2 = filename2;
		lastUpload2 = last2;
	}

	public void setLastUpload(String filename, byte[] last, String pass1, String filename2, byte[] last2, String pass2) {
		lastUploadFilename = filename;
		lastUpload = last;

		lastUploadFilename2 = filename2;
		lastUpload2 = last2;

		password1 = pass1;
		password2 = pass2;
		logger.info("lastUpload size=" + ((lastUpload == null) ? "null" : Integer.toString(lastUpload.length)));
		logger.info("lastUpload2 size=" + ((lastUpload2 == null) ? "null" : Integer.toString(lastUpload2.length)));
	}

	public byte[] getlastUpload() {
		return lastUpload;
	}

	public String getPassword1() {
		return password1;
	}

	public String getPassword2() {
		return password2;
	}

	public byte[] getInput2() {
		return input2;
	}

	public byte[] getlastUpload2() {
		return lastUpload2;
	}

	public String getlastUploadFilename() {
		return lastUploadFilename;
	}

	public String getInput2Filename() {
		return input2Filename;
	}

	public String getlastUploadFilename2() {
		return lastUploadFilename2;
	}

	public File getTestkitFile() {
		return new File(Installation.installation().warHome() + File.separator + "toolkitx" + File.separator
				+ "testkit");
	}

	/**
	 * Reset linkage to XdsTest
	 */
	public void clear() {
		xt = null;
		res = null;
		// transactionSettings = null;
	}

	boolean lessThan(String a, String b) {
		return a.compareTo(b) == -1;
	}

	// public void saveLogMapInSessionCache(LogMap log, XdstestLogId id) throws
	// XdsException {
	// new RawLogCache(getTomcatSessionCache()).logOut(id, log);
	//
	// if (mesaSessionExists()) {
	// new RawLogCache(mesaSessionCache).logOut(id, log);
	// }
	// }

	public Metadata getLastMetadata() {
		return lastMetadata;
	}

	public void setLastMetadata(Metadata lastMetadata) {
		this.lastMetadata = lastMetadata;
	}

	/*
	 * Manage the environment, choice of keystore and codes.xml
	 */

	public File getEnvironmentDir() throws EnvironmentNotSelectedException {
		try {
			return EnvSetting.getEnvSetting(sessionId).getEnvDir();
		} catch (Exception e) {
			throw new EnvironmentNotSelectedException("", e);
		}
	}

	public File getEnvironment() throws EnvironmentNotSelectedException {
		return getEnvironmentDir();
	}

	public File getCodesFile() throws EnvironmentNotSelectedException {
		if (getEnvironmentDir() == null)
			return null; // new File(Installation.installation().warHome() +
							// File.separator + "toolkitx" + File.separator +
							// "codes" + File.separator + "codes.xml");
		File f = new File(getEnvironmentDir() + File.separator + "codes.xml");
		if (f.exists())
			return f;
		return null;
	}

	public File getKeystoreDir() throws EnvironmentNotSelectedException {
		File f = new File(getEnvironmentDir() + File.separator + "keystore");
		if (f.exists() && f.isDirectory())
			return f;
		throw new EnvironmentNotSelectedException("");
	}

	public File getKeystore() throws EnvironmentNotSelectedException {
		File kd = getKeystoreDir();
		return new File(kd + File.separator + "keystore");
	}

	/*
	 * DSIG_keystore_password=changeit
	 * DSIG_keystore_alias=1
	 * keystore_url=keystore
	 */

	public String getKeystorePassword() throws IOException, EnvironmentNotSelectedException {
		Properties p = new Properties();
		File f = new File(getKeystoreDir() + File.separator + "keystore.properties");
		if (!f.exists())
			return "";
		FileInputStream fis = new FileInputStream(f);
		p.load(fis);
		return p.getProperty("keyStorePassword");
	}

	public String getKeystoreAlias() throws IOException, EnvironmentNotSelectedException {
		Properties p = new Properties();
		File f = new File(getKeystoreDir() + File.separator + "keystore.properties");
		if (!f.exists())
			return "";
		FileInputStream fis = new FileInputStream(f);
		p.load(fis);
		return p.getProperty("DSIG_keystore_alias");
	}
	
	

	public List<String> getEnvironmentNames() {
		logger.debug(getId() + ": " + "getEnvironmentNames");
		List<String> names = new ArrayList<String>();

		File k = Installation.installation().environmentFile(); // propertyServiceManager().getPropertyManager().getExternalCache()
																// +
																// File.separator
																// +
																// "environment");
		if (!k.exists() || !k.isDirectory())
			return names;
		File[] files = k.listFiles();
		for (File file : files)
			if (file.isDirectory())
				names.add(file.getName());

		return names;
	}

	public TkProps tkProps() {
		try {
			return TkLoader.tkProps(Installation.installation().getTkPropsFile());
		} catch (Throwable t) {
			return new TkProps();
		}
	}

	/**
	 * Sets name of current environment (for this session)
	 * 
	 * @throws
	 */
	public void setEnvironment(String name) {
		if (name == null || name.equals(""))
			return;
		logger.debug(getId() + ": " + " Environment set to " + name);
		setEnvironment(name, Installation.installation().propertyServiceManager().getPropertyManager()
				.getExternalCache());
	}

	public void setEnvironment(String name, String externalCache) {
		File k = Installation.installation().environmentFile(name);
		if (!k.exists() || !k.isDirectory())
			k = null;
		currentEnvironmentName = name;
		System.setProperty("XDSCodesFile", k.toString() + File.separator + "codes.xml");
		new EnvSetting(sessionId, name, k);
		logger.debug(getId() + ": " + "Environment set to " + k);
	}

	public String getCurrentEnvironment() {
		return currentEnvironmentName;
	}

	public SessionPropertyManager getSessionProperties() {
		if (mesaSessionName == null)
			return null;

		if (sessionProperties == null) {
			File testLogCache;
			try {
				testLogCache = Installation.installation().propertyServiceManager().getTestLogCache();
			} catch (Exception e) {
				return null;
			}

			sessionProperties = new SessionPropertyManager(testLogCache.toString());
		}

		return sessionProperties;
	}

	public Map<String, String> getSessionPropertiesAsMap() {
		logger.debug(": " + "getSessionProperties()");
		return getSessionProperties().asMap();
	}

	public File getToolkitFile() {
		if (toolkit == null)
			toolkit = Installation.installation().toolkitxFile(); // new
																	// File(Installation.installation().warHome()
																	// +
																	// File.separator
																	// +
																	// "toolkitx");
		return toolkit;
	}

	public String allocateNewPid(String assigningAuthority) {
		// return new PidGenerator(assigningAuthority).get();
		return "x";
	}

	public void deleteSim(String simulatorId) {
		try {
			logger.info("Delete sim " + simulatorId);
			SimDb simdb = new SimDb(Installation.installation().simDbFile(), simulatorId, null, null);
			File simdir = simdb.getIpDir();
			Io.delete(simdir);
		} catch (IOException e) {
			// doesn't exist - ok
		} catch (NoSimException e) {
			// doesn't exist - ok
		}
	}

}
