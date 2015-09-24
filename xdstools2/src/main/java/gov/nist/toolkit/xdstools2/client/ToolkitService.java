package gov.nist.toolkit.xdstools2.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.nist.toolkit.actorfactory.client.*;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@RemoteServiceRelativePath("toolkit")
public interface ToolkitService extends RemoteService  {
	

	public TkProps getTkProps() throws NoServletSessionException;
	
	/* Test management */
	public Map<String, Result> getTestResults(List<String> testIds, String testSession) throws NoServletSessionException ;
	public Map<String, String> getCollectionNames(String collectionSetName) throws Exception;
	public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception;
	public String getTestReadme(String test) throws Exception;
	public List<String> getTestIndex(String test) throws Exception;
	public List<Result> runMesaTest(String mesaTestSession, SiteSpec siteSpec, String testName, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure) throws NoServletSessionException ;
	public boolean isPrivateMesaTesting() throws NoServletSessionException ;
	public List<String> getMesaTestSessionNames() throws Exception;
	public boolean addMesaTestSession(String name) throws Exception;
	public boolean delMesaTestSession(String name) throws Exception;

	/* Simulator Management */
	public List<String> getActorTypeNames() throws NoServletSessionException ;
	public Simulator getNewSimulator(String actorTypeName, SimId simId) throws Exception;
	public List<SimulatorConfig> getSimConfigs(List<SimId> ids) throws Exception;
	List<SimulatorConfig> getAllSimConfigs(String user) throws Exception;
	public String putSimConfig(SimulatorConfig config) throws Exception;
	public String deleteConfig(SimulatorConfig config) throws Exception;
	public Map<String, SimId> getActorSimulatorNameMap() throws NoServletSessionException;
//	public List<String> getSimulatorTransactionNames(String simid) throws Exception;
	public int removeOldSimulators() throws NoServletSessionException;
	List<SimulatorStats> getSimulatorStats(List<SimId> simid) throws Exception;
	List<Pid> getPatientIds(SimId simId) throws Exception;
	String addPatientIds(SimId simId, List<Pid> pids) throws Exception;
	boolean deletePatientIds(SimId simId, List<Pid> pids) throws Exception;

	String setToolkitProperties(Map<String, String> props) throws Exception;
	Map<String, String> getToolkitProperties() throws NoServletSessionException ;
	boolean reloadPropertyFile() throws NoServletSessionException ;

	String getTransactionRequest(SimId simName, String actor, String trans, String event)  throws NoServletSessionException;
	String getTransactionResponse(SimId simName, String actor, String trans, String event)  throws NoServletSessionException;
	String getTransactionLog(SimId simName, String actor, String trans, String event)  throws NoServletSessionException;
	List<String> getTransactionsForSimulator(SimId simName) throws Exception;
//	List<String> getActorNames();
	MessageValidationResults executeSimMessage(String simFileSpec) throws NoServletSessionException;
	
	void renameSimFile(String simFileSpec, String newSimFileSpec) throws Exception;
	void deleteSimFile(String simFileSpec) throws Exception;
	String getSimulatorEndpoint() throws NoServletSessionException;
	List<Result> getSelectedMessage(String simFilename) throws NoServletSessionException;
	List<Result> getSelectedMessageResponse(String simFilename) throws NoServletSessionException;
	@Deprecated
	String getClientIPAddress();
	
	List<String> getTransInstances(SimId simid, String actor, String trans)  throws Exception;
	
	List<Result> getLastMetadata();
	String getLastFilename();
	String getTimeAndDate();
	
	MessageValidationResults validateMessage(ValidationContext vc) throws NoServletSessionException, EnvironmentNotSelectedClientException;
//	MessageValidationResults validateMessage(ValidationContext vc, String simFileName) throws NoServletSessionException, EnvironmentNotSelectedClientException;
	
	List<String> getSiteNames(boolean reload, boolean simAlso) throws NoServletSessionException ;
	List<String> getRegistryNames() throws Exception;
	List<String> getRepositoryNames() throws Exception; 
	List<String> getRGNames() throws NoServletSessionException ;
	List<String> getIGNames() throws NoServletSessionException ;
	List<String> getTestdataSetListing(String testdataSetName)  throws NoServletSessionException;
	CodesResult getCodesConfiguration() throws NoServletSessionException ;
	TransactionOfferings getTransactionOfferings() throws Exception;
	
	List<String> reloadSites(boolean simAlso) throws Exception;
	List<String> reloadExternalSites() throws Exception;
	Site getSite(String siteName) throws Exception;
	Collection<Site> getAllSites() throws Exception;
	String saveSite(Site site) throws Exception;
	String deleteSite(String siteName) throws Exception;
	
	List<Result> getSSandContents(SiteSpec site, String ssuid) throws NoServletSessionException ;
	List<Result> srcStoresDocVal(SiteSpec site, String ssuid) throws NoServletSessionException ;
	List<Result> findDocuments(SiteSpec site, String pid, boolean onDemand) throws NoServletSessionException ;
	List<Result> findDocumentsByRefId(SiteSpec site, String pid, List<String> refIds) throws NoServletSessionException ;
	List<Result> findFolders(SiteSpec site, String pid) throws NoServletSessionException ;
	List<Result> findPatient(SiteSpec site, String firstName,
			String secondName, String lastName, String suffix, String gender,
			String dob, String ssn, String pid, String homeAddress1,
			String homeAddress2, String homeCity, String homeState,
			String homeZip, String homeCountry, String mothersFirstName, String mothersSecondName,
			String mothersLastName, String mothersSuffix, String homePhone,
			String workPhone, String principleCareProvider, String pob,
			String pobAddress1, String pobAddress2, String pobCity, String Country,
			String pobState, String pobZip);
	List<Result> getDocuments(SiteSpec site, AnyIds ids) throws NoServletSessionException ;
	List<Result> getFolders(SiteSpec site, AnyIds aids) throws NoServletSessionException ;
	List<Result> getFoldersForDocument(SiteSpec site, AnyIds aids) throws NoServletSessionException ;
	List<Result> getFolderAndContents(SiteSpec site, AnyIds aids) throws NoServletSessionException ;
	List<Result> getObjects(SiteSpec site, ObjectRefs ids) throws NoServletSessionException ;
	List<Result> getAssociations(SiteSpec site, ObjectRefs ids) throws NoServletSessionException ;
	List<Result> getSubmissionSets(SiteSpec site, AnyIds ids) throws NoServletSessionException ;
	List<Result> registerAndQuery(SiteSpec site, String pid) throws NoServletSessionException ;
	List<Result> getRelated(SiteSpec site, ObjectRef or, List<String> assocs) throws NoServletSessionException ;
	List<Result> retrieveDocument(SiteSpec site, Uids uids) throws Exception;
	List<Result> submitRegistryTestdata(SiteSpec site, String datasetName, String pid) throws NoServletSessionException ;	
	List<Result> submitRepositoryTestdata(SiteSpec site, String datasetName, String pid) throws NoServletSessionException ;	
	List<Result> submitXDRTestdata(SiteSpec site, String datasetName, String pid) throws NoServletSessionException ;	
	List<Result> provideAndRetrieve(SiteSpec site, String pid) throws NoServletSessionException ;
	List<Result> lifecycleValidation(SiteSpec site, String pid) throws NoServletSessionException ;
	List<Result> folderValidation(SiteSpec site, String pid) throws NoServletSessionException ;
	
//	List<Result> mpqFindDocuments(SiteSpec site, String pid, List<String> classCodes, List<String> hcftCodes, List<String> eventCodes) throws NoServletSessionException;
	List<Result> mpqFindDocuments(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) throws NoServletSessionException;
	List<Result> getAll(SiteSpec site, String pid, Map<String, List<String>> codesSpec) throws NoServletSessionException;

	TestLogs getRawLogs(XdstestLogId logId) throws NoServletSessionException ;
	
	String getAdminPassword() throws NoServletSessionException ;
	
	String getTestplanAsText(String testname, String section) throws Exception;
	
	public String getImplementationVersion() throws NoServletSessionException ;
	
	public List<String> getUpdateNames() throws NoServletSessionException ;
	public List<String> getTestlogListing(String sessionName) throws Exception;
	public List<Result> getLogContent(String sessionName, String testName) throws Exception;
	
	public List<RegistryStatus> getDashboardRegistryData() throws Exception;
	public List<RepositoryStatus> getDashboardRepositoryData() throws Exception;
	
	public List<String> getSiteNamesWithRG() throws Exception;

	public String reloadSystemFromGazelle(String systemName) throws Exception;
	public boolean isGazelleConfigFeedEnabled() throws NoServletSessionException ;
	public List<String> getEnvironmentNames() throws NoServletSessionException;
	public String setEnvironment(String name) throws NoServletSessionException;
	public String getCurrentEnvironment() throws NoServletSessionException;
	public String getDefaultEnvironment() throws NoServletSessionException ;
//	public String getToolkitEnableNwHIN();
	public String getDefaultAssigningAuthority() throws NoServletSessionException ;
	public String getAttributeValue(String username, String attName) throws Exception;
	public void setAttributeValue(String username, String attName, String attValue) throws Exception;
	
	public Map<String, String> getSessionProperties() throws NoServletSessionException;
	public void setSessionProperties(Map<String, String> props) throws NoServletSessionException;
	Pid createPid(String assigningAuthority) throws NoServletSessionException;
	String getAssigningAuthority() throws Exception;
	List<String> getAssigningAuthorities() throws Exception;
	List<Result> sendPidToRegistry(SiteSpec site, Pid pid) throws NoServletSessionException;

	public String setMesaTestSession(String sessionName) throws NoServletSessionException ;
	public String getNewPatientId(String assigningAuthority) throws NoServletSessionException ;
}
