	package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactoryFactory;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.directsim.DirectServiceManager;
import gov.nist.toolkit.directsim.DirectUserManager;
import gov.nist.toolkit.directsim.client.ContactRegistrationData;
import gov.nist.toolkit.directsim.client.DirectRegistrationData;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyServiceManager;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.session.server.DirectConfigManager;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.QueryServiceManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.utilities.xml.SchemaValidation;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdstools2.client.NoServletSessionException;
import gov.nist.toolkit.xdstools2.client.RegistryStatus;
import gov.nist.toolkit.xdstools2.client.RepositoryStatus;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.server.serviceManager.DashboardServiceManager;
import gov.nist.toolkit.xdstools2.server.serviceManager.GazelleServiceManager;
import gov.nist.toolkit.xdstools2.server.serviceManager.SimulatorServiceManager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ToolkitServiceImpl extends RemoteServiceServlet implements
ToolkitService {
	static String schematronHome = null;
	ServletContext context = null;
//	static File warHome = null;
	
	static Logger logger = Logger.getLogger(ToolkitServiceImpl.class);

	// Individual service requests from browser are delegated to one of these
	
	// ServiceManagers for execution
	public QueryServiceManager queryServiceManager; 
	public SiteServiceManager siteServiceManager;
	public DashboardServiceManager dashboardServiceManager;
	public GazelleServiceManager gazelleServiceManager;

	// Next two constructors exist to initialize MessageValidatorFactoryFactory which olds
	// a reference to an instance of this class. This is necessary to get around a circular
	// reference in the build tree
	
	public ToolkitServiceImpl() {
			siteServiceManager = SiteServiceManager.getSiteServiceManager();   // One copy shared between sessions
			System.out.println("MessageValidatorFactory()");
			if (MessageValidatorFactoryFactory.messageValidatorFactory2I == null) {
				MessageValidatorFactoryFactory.messageValidatorFactory2I = new MessageValidatorFactory("a");
			}
	}
	
	// Direct Services
	public DirectRegistrationData directRegistration(DirectRegistrationData reg) throws NoServletSessionException, Exception { 
		new DirectServiceManager().directRegistration(session(), reg); 
		return reg;
		}
	public ContactRegistrationData contactRegistration(ContactRegistrationData reg) throws NoServletSessionException, Exception { 
		new DirectUserManager().contactRegistration(reg); 
		return reg;
		}
	public ContactRegistrationData saveCertFromUpload(ContactRegistrationData reg, String directAddr)  throws NoServletSessionException, Exception {
		byte[] cert = session().getlastUpload();
		new DirectUserManager().saveCertFromUpload(reg, directAddr, cert); 
		return reg;
	}
	public ContactRegistrationData loadDirectRegistration(String contact) throws Exception {
		return new DirectUserManager().load(contact);
	}
	public ContactRegistrationData deleteDirect(ContactRegistrationData contact, DirectRegistrationData direct) throws NoServletSessionException, Exception {
		return new DirectUserManager().deleteDirect(contact, direct);
	}
	public String toolkitPubCert()  throws NoServletSessionException { return new DirectServiceManager(session()).toolkitPubCert(); }
	public List<Result> directSend(Map<String, String> parms) throws NoServletSessionException { return new DirectServiceManager(session()).directSend(parms); }
	public List<String> getEncryptionCertDomains() { return new DirectConfigManager(Installation.installation().externalCache()).getEncryptionCertDomains(); }


	// Site Services
	public List<String> getSiteNames(boolean reload, boolean simAlso)  throws NoServletSessionException { return siteServiceManager.getSiteNames(session().getId(), reload, simAlso); }
	public Collection<Site> getAllSites() throws Exception { return siteServiceManager.getAllSites(session().getId()); }
	public List<String> reloadSites(boolean simAlso) throws FactoryConfigurationError, Exception { return siteServiceManager.reloadSites(session().getId(), simAlso); }
	public Site getSite(String siteName) throws Exception { return siteServiceManager.getSite(session().getId(), siteName); }
	public String saveSite(Site site) throws Exception { return siteServiceManager.saveSite(session().getId(), site); }
	public String deleteSite(String siteName) throws Exception { return siteServiceManager.deleteSite(session().getId(), siteName); }
	public String getHome() throws Exception { return session().getHome(); }
	public List<String> getUpdateNames()  throws NoServletSessionException { return siteServiceManager.getUpdateNames(session().getId()); }
	public TransactionOfferings getTransactionOfferings() throws Exception { return siteServiceManager.getTransactionOfferings(session().getId()); }
	public List<String> reloadExternalSites() throws FactoryConfigurationError, Exception { return siteServiceManager.reloadExternalSites(session().getId()); }
	public List<String> getRegistryNames()  throws NoServletSessionException { return siteServiceManager.getRegistryNames(session().getId()); }
	public List<String> getRepositoryNames()  throws NoServletSessionException { return siteServiceManager.getRepositoryNames(session().getId()); }
	public List<String> getRGNames()  throws NoServletSessionException { return siteServiceManager.getRGNames(session().getId()); }
	public List<String> getIGNames()  throws NoServletSessionException { return siteServiceManager.getIGNames(session().getId()); }
	public List<String> getActorTypeNames()  throws NoServletSessionException { return siteServiceManager.getActorTypeNames(session().getId()); }
	public List<String> getSiteNamesWithRG() throws Exception { return siteServiceManager.getSiteNamesWithRG(session().getId()); }


	// Query Services
	public List<Result> registerAndQuery(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().registerAndQuery(site, pid); }
	public List<Result> lifecycleValidation(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().lifecycleValidation(site, pid); }
	public List<Result> folderValidation(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().folderValidation(site, pid); }
	public List<Result> submitRegistryTestdata(SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitRegistryTestdata(site, datasetName, pid); }
	public List<Result> submitRepositoryTestdata(SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitRepositoryTestdata(site, datasetName, pid); }
	public List<Result> submitXDRTestdata(SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitXDRTestdata(site, datasetName, pid); }
	public List<Result> provideAndRetrieve(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().provideAndRetrieve(site, pid); }
	public List<Result> findDocuments(SiteSpec site, String pid, boolean onDemand) throws NoServletSessionException  { return session().queryServiceManager().findDocuments(site, pid, onDemand); }
	public List<Result> getDocuments(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getDocuments(site, aids); }
	public List<Result> findFolders(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().findFolders(site, pid); }
	public List<Result> getFolders(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFolders(site, aids); }
	public List<Result> getFoldersForDocument(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFoldersForDocument(site, aids); }
	public List<Result> getFolderAndContents(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFolderAndContents(site, aids); }
	public List<Result> getAssociations(SiteSpec site, ObjectRefs ids) throws NoServletSessionException  { return session().queryServiceManager().getAssociations(site, ids); }
	public List<Result> getObjects(SiteSpec site, ObjectRefs ids) throws NoServletSessionException  { return session().queryServiceManager().getObjects(site, ids); }
	public List<Result> getSubmissionSets(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getSubmissionSets(site, aids); }
	public List<Result> getSSandContents(SiteSpec site, String ssid) throws NoServletSessionException  { return session().queryServiceManager().getSSandContents(site, ssid); }
	public List<Result> srcStoresDocVal(SiteSpec site, String ssid) throws NoServletSessionException  { return session().queryServiceManager().srcStoresDocVal(site, ssid); }
	public List<Result> retrieveDocument(SiteSpec site, Uids uids) throws Exception { return session().queryServiceManager().retrieveDocument(site, uids); }
	public List<Result> getRelated(SiteSpec site, ObjectRef or,	List<String> assocs) throws NoServletSessionException  { return session().queryServiceManager().getRelated(site, or, assocs); }

	public List<Result> findPatient(SiteSpec site, String firstName,
			String secondName, String lastName, String suffix, String gender,
			String dob, String ssn, String pid, String homeAddress1,
			String homeAddress2, String homeCity, String homeState,
			String homeZip, String homeCountry, String mothersFirstName, String mothersSecondName,
			String mothersLastName, String mothersSuffix, String homePhone,
			String workPhone, String principleCareProvider, String pob,
			String pobAddress1, String pobAddress2, String pobCity,
			String pobState, String pobZip, String pobCountry) {
		return queryServiceManager.findPatient(site, firstName, secondName,  lastName, suffix, gender, dob, ssn, pid,  
				homeAddress1, homeAddress2, homeCity, homeState, homeZip, homeCountry,
				mothersFirstName, mothersSecondName, mothersLastName, mothersSuffix, 
				homePhone, workPhone, principleCareProvider, 
				pob, pobAddress1, pobAddress2, pobCity,pobState, pobZip, pobCountry);
	}
	public List<Result> mpqFindDocuments(SiteSpec site, String pid,
			List<String> classCodes, List<String> hcftCodes,
			List<String> eventCodes) {
		return queryServiceManager.mpqFindDocuments(site, pid, classCodes, hcftCodes,
				eventCodes);
	}
	
	public List<Result> getLastMetadata() { return queryServiceManager.getLastMetadata(); }

	// Test Service
	public Map<String, Result> getTestResults(List<String> testIds, String testSession )  throws NoServletSessionException { return session().xdsTestServiceManager().getTestResults(testIds, testSession); }
	public String setMesaTestSession(String sessionName)  throws NoServletSessionException { session().xdsTestServiceManager().setMesaTestSession(sessionName); return sessionName;}
	public List<String> getMesaTestSessionNames() throws Exception { return session().xdsTestServiceManager().getMesaTestSessionNames(); }
	public boolean addMesaTestSession(String name) throws Exception { return session().xdsTestServiceManager().addMesaTestSession(name); }
	public String getNewPatientId(String assigningAuthority)  throws NoServletSessionException { return session().xdsTestServiceManager().getNewPatientId(assigningAuthority); }
	public String getTestReadme(String test) throws Exception { return session().xdsTestServiceManager().getTestReadme(test); }
	public List<String> getTestIndex(String test) throws Exception { return session().xdsTestServiceManager().getTestIndex(test); }
	public Map<String, String> getCollectionNames(String collectionSetName) throws Exception { return session().xdsTestServiceManager().getCollectionNames(collectionSetName); }
	public List<Result> getLogContent(String sessionName, String testName) throws Exception { return session().xdsTestServiceManager().getLogContent(sessionName, testName); }
	public List<Result> runMesaTest(String mesaTestSession, SiteSpec siteSpec, String testName, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure)  throws NoServletSessionException {
		return session().xdsTestServiceManager().runMesaTest(mesaTestSession, siteSpec, testName, sections, params, null, stopOnFirstFailure); 
	}
	public TestLogs getRawLogs(XdstestLogId logId)  throws NoServletSessionException { return session().xdsTestServiceManager().getRawLogs(logId); }
	public List<String> getTestdataSetListing(String testdataSetName)  throws NoServletSessionException { return session().xdsTestServiceManager().getTestdataSetListing(testdataSetName); }
	public String getTestplanAsText(String testname, String section) throws Exception { return session().xdsTestServiceManager().getTestplanAsText(testname, section); }
	public CodesResult getCodesConfiguration()  throws NoServletSessionException { return session().xdsTestServiceManager().getCodesConfiguration(); }
	public List<String> getTestlogListing(String sessionName) throws Exception { return session().xdsTestServiceManager().getTestlogListing(sessionName); }
	public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception { return session().xdsTestServiceManager().getCollection(collectionSetName, collectionName); }
	public boolean isPrivateMesaTesting()  throws NoServletSessionException { return session().xdsTestServiceManager().isPrivateMesaTesting(); }

	
	// Gazelle Service
	public String reloadSystemFromGazelle(String systemName) throws Exception { return new GazelleServiceManager(session()).reloadSystemFromGazelle(systemName); }

	// Session
	public List<String> getEnvironmentNames() throws NoServletSessionException { return session().getEnvironmentNames(); }
	public String setEnvironment(String name) throws NoServletSessionException { session().setEnvironment(name); return name; }
	public String getCurrentEnvironment() throws NoServletSessionException { return session().getCurrentEnvironment(); }
	public Map<String, String> getSessionProperties() throws NoServletSessionException { return session().getSessionPropertiesAsMap(); }
	public void setSessionProperties(Map<String, String> props) throws NoServletSessionException { session().setSessionProperties(props); }
	
	// Property Service
	public TkProps getTkProps() throws NoServletSessionException { return session().tkProps(); }
	public String getDefaultEnvironment()  throws NoServletSessionException  { return Installation.installation().propertyServiceManager().getDefaultEnvironment(); }
	public String getDefaultAssigningAuthority()  throws NoServletSessionException { return Installation.installation().propertyServiceManager().getDefaultAssigningAuthority(); }
	public String getImplementationVersion() throws NoServletSessionException  { return Installation.installation().propertyServiceManager().getImplementationVersion(); }
	public Map<String, String> getToolkitProperties()  throws NoServletSessionException { return Installation.installation().propertyServiceManager().getToolkitProperties(); }
	public boolean isGazelleConfigFeedEnabled() throws NoServletSessionException  { return SiteServiceManager.getSiteServiceManager().useGazelleConfigFeed(session().id()); }
//	public String getToolkitEnableNwHIN() { return propertyServiceManager.getToolkitEnableNwHIN(); }
	public String setToolkitProperties(Map<String, String> props) throws Exception { return setToolkitPropertiesImpl(props); }
	public String getAdminPassword() throws NoServletSessionException  { return Installation.installation().propertyServiceManager().getAdminPassword(); }
	public boolean reloadPropertyFile() throws NoServletSessionException  { return Installation.installation().propertyServiceManager().reloadPropertyFile(); }
	public String getAttributeValue(String username, String attName) throws Exception { return Installation.installation().propertyServiceManager().getAttributeValue(username, attName); }
	public void setAttributeValue(String username, String attName, String attValue) throws Exception { Installation.installation().propertyServiceManager().setAttributeValue(username, attName, attValue); }

	
	// Simulator Service
	public String putSimConfig(SimulatorConfig config) throws Exception { return new SimulatorServiceManager(session()).putSimConfig(config); }
	public String deleteConfig(SimulatorConfig config) throws Exception { return new SimulatorServiceManager(session()).deleteConfig(config); }
	public void renameSimFile(String simFileSpec, String newSimFileSpec) throws Exception { new SimulatorServiceManager(session()).renameSimFile(simFileSpec, newSimFileSpec); }
	public String getSimulatorEndpoint() throws NoServletSessionException { return new SimulatorServiceManager(session()).getSimulatorEndpoint(); }
	public MessageValidationResults executeSimMessage(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).executeSimMessage(simFileSpec); }
	public List<String> getTransInstances(String simid, String xactor, String trans) throws Exception { return new SimulatorServiceManager(session()).getTransInstances(simid, xactor, trans); }
	public String getTransactionRequest(String simid, String actor, String trans, String event) throws NoServletSessionException { return new SimulatorServiceManager(session()).getTransactionRequest(simid, actor, trans, event); }
	public String getTransactionResponse(String simid, String actor, String trans, String event) throws NoServletSessionException { return new SimulatorServiceManager(session()).getTransactionResponse(simid, actor, trans, event); }
	public int removeOldSimulators() throws NoServletSessionException { return new SimulatorServiceManager(session()).removeOldSimulators(); }
	public List<Result> getSelectedMessage(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).getSelectedMessage(simFileSpec); }
	public List<Result> getSelectedMessageResponse(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).getSelectedMessageResponse(simFileSpec); }
	public Map<String, String> getActorSimulatorNameMap() throws NoServletSessionException { return new SimulatorServiceManager(session()).getActorSimulatorNameMap(); }
	public MessageValidationResults validateMessage(ValidationContext vc) throws NoServletSessionException { return new SimulatorServiceManager(session()).validateMessage(vc); }
	public MessageValidationResults validateMessage(ValidationContext vc, String simFileName) throws NoServletSessionException { return new SimulatorServiceManager(session()).validateMessage(vc, simFileName); }
	public List<SimulatorConfig> getSimConfigs(List<String> ids) throws Exception { return new SimulatorServiceManager(session()).getSimConfigs(ids); }
	public List<SimulatorConfig> getNewSimulator(String actorTypeName) throws Exception { return new SimulatorServiceManager(session()).getNewSimulator(actorTypeName); }
	public void deleteSimFile(String simFileSpec) throws Exception { new SimulatorServiceManager(session()).deleteSimFile(simFileSpec); }
	public List<String> getTransactionsForSimulator(String simid) throws Exception { return new SimulatorServiceManager(session()).getTransactionsForSimulator(simid); }
	public String getTransactionLog(String simid, String actor, String trans, String event) throws NoServletSessionException { return new SimulatorServiceManager(session()).getTransactionLog(simid, actor, trans, event); }

	// Dashboard Service
	public List<RegistryStatus> getDashboardRegistryData() throws Exception { return new DashboardServiceManager(session()).getDashboardRegistryData(); }
	public List<RepositoryStatus> getDashboardRepositoryData() throws Exception { return new DashboardServiceManager(session()).getDashboardRepositoryData(); }

	// Other support calls
	
	public String setToolkitPropertiesImpl(Map<String, String> props)
			throws Exception {
		logger.debug(": " + "setToolkitProperties");

		// verify External_Cache points to a writable directory
		String eCache = props.get("External_Cache");
		File eCacheFile = new File(eCache);
		if (!eCacheFile.exists() || !eCacheFile.isDirectory()) 
			throw new IOException("Cannot save toolkit properties: property External_Cache does not point to an existing directory");
		if (!eCacheFile.canWrite())
			throw new IOException("Cannot save toolkit properties: property External_Cache points to a directory that is not writable");

		File warhome = Installation.installation().warHome();
		new PropertyServiceManager(warhome).getPropertyManager().update(props);
		reloadPropertyFile();
		Installation.installation().externalCache(eCacheFile);
		try {
			TkLoader.tkProps(Installation.installation().getTkPropsFile());
		} catch (Throwable t) {
			
		}
		return "";
	}


	public ServletContext servletContext() {
		// this gets called from the initialization section of SimServlet
		// for access to properties.  This code is not expected to work correct.
		// Just don't throw exceptions that are not helpful
		try {
			if (context == null)
				context = getServletContext();
		} catch (Exception e) {

		}
		if (context != null && Installation.installation().warHome() == null) {
			
			File warHome = new File(context.getRealPath("/"));
			System.setProperty("warHome", warHome.toString());
			System.out.println("warHome [ToolkitServiceImpl]: " + warHome);
			Installation.installation().warHome(warHome);
		}
		return context;
	}

	// Used only for non-servlet use (Dashboard is good example)
	static public final String sessionVarName = "MySession";
	String sessionID = null;
	
	public String getSessionId() {
		if (sessionID != null)
			return sessionID;
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession hsession = request.getSession();
		return hsession.getId();
	}
	
	public String getSessionIdIfAvailable() {
		try {
			return getSessionId();
		} catch (Exception e) {
			return "";
		}
	}


	Session standAloneSession = null;  // needed for standalone use not part of servlet

	public void setStandAloneSession(Session s) {
		standAloneSession = s;
	}
	
	// This exception is passable to the GUI.  The server side exception
	// is NoSessionException
	public Session session() throws NoServletSessionException {
		Session s = getSession();
		if (s == null)
			throw new NoServletSessionException("");
		return s;
	}
	

	public Session getSession() {
		HttpServletRequest request = this.getThreadLocalRequest();
		return getSession(request);
	}
		
	public Session getSession(HttpServletRequest request) {
		if (request == null && standAloneSession != null) {
			// not running interactively - maybe part of Dashboard
			return standAloneSession;
		}

		Session s = null;
		HttpSession hsession = null;
		if (request != null) {
			hsession = request.getSession();
			s = (Session) hsession.getAttribute(sessionVarName);
			if (s != null)
				return s;
			servletContext();
		}
		
		// Force short session timeout for testing
//		hsession.setMaxInactiveInterval(60/4);    // one quarter minute
		
		//******************************************
		//
		// New session object to be created
		//
		//******************************************
		File warHome = null;
		if (s == null) {
			ServletContext sc = servletContext();
			warHome = Installation.installation().warHome();
			if (sc != null && warHome == null) {
				warHome = new File(sc.getRealPath("/"));
				Installation.installation().warHome(warHome);
				System.setProperty("warHome", warHome.toString());
				System.out.print("warHome [ToolkitServiceImp]: " + warHome);
				Installation.installation().warHome(warHome);
			}
			if (warHome != null)
				System.setProperty("warHome", warHome.toString());

			if (warHome != null) {
				s = new Session(warHome, siteServiceManager, getSessionId());
				if (hsession != null) {
					s.setSessionId(hsession.getId());
					s.addSession();
					hsession.setAttribute(sessionVarName, s);
				} else
					s.setSessionId("mysession");
			}
		}

		if (request != null) {
			if (s.getIpAddr() == null) {
				s.setIpAddr(request.getRemoteHost());
			}

			s.setServerSpec(request.getLocalName(),
					String.valueOf(request.getLocalPort()));
		}

		if (warHome != null) {
			if (SchemaValidation.toolkitSchemaLocation == null) {
				SchemaValidation.toolkitSchemaLocation = warHome + File.separator + "toolkitx" + File.separator + "schema";
			}
		}

		return s;
	}

	public String getLastFilename() {
		return getSession().getlastUploadFilename();
	}

	public String getTimeAndDate() {
		return new Date().toString();
	}

	@Deprecated
	public String getClientIPAddress() {
		return getSession().ipAddr;
	}

}
