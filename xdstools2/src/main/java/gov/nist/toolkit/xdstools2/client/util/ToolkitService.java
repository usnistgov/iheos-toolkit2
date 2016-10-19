package gov.nist.toolkit.xdstools2.client.util;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.services.client.*;
import gov.nist.toolkit.session.client.ConformanceSessionValidationStatus;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.session.client.TestPartFileDTO;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdstools2.shared.NoServletSessionException;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.*;
import gov.nist.toolkit.xdstools2.shared.command.InitializationResponse;
import gov.nist.toolkit.xdstools2.shared.RegistryStatus;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@RemoteServiceRelativePath("toolkit")
public interface ToolkitService extends RemoteService  {

	TkProps getTkProps() throws NoServletSessionException;

	ConformanceSessionValidationStatus validateConformanceSession(String testSession, String siteName) throws Exception;
	Collection<String> getSitesForTestSession(CommandContext context) throws Exception;
	InitializationResponse getInitialization() throws Exception;
	String getAssignedSiteForTestSession(String testSession) throws Exception;
	void setAssignedSiteForTestSession(SetAssignedSiteForTestSessionRequest request) throws Exception;


	/* Test management */
	 Map<String, Result> getTestResults(List<TestInstance> testInstances, String testSession) throws NoServletSessionException ;
	 LogFileContentDTO getTestLogDetails(String sessionName, TestInstance testInstance) throws Exception;
	 Map<String, String> getCollectionNames(String collectionSetName) throws Exception;
	 List<String> getCollectionMembers(String collectionSetName, String collectionName) throws Exception;
	List<TestCollectionDefinitionDAO> getTestCollections(String collectionSetName) throws Exception;
	 Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception;
	 String getTestReadme(String test) throws Exception;
	 List<String> getTestIndex(String test) throws Exception;
	 List<Result> runMesaTest(String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure) throws Exception ;
	 TestOverviewDTO runTest(String environment, String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, Map<String, String> params, boolean stopOnFirstFailure) throws Exception;
	 boolean isPrivateMesaTesting() throws NoServletSessionException ;
	 List<String> getMesaTestSessionNames(CommandContext request) throws Exception;
	 boolean addMesaTestSession(String name) throws Exception;
	 boolean delMesaTestSession(String name) throws Exception;

	/* Simulator Management */
	 List<String> getActorTypeNames() throws NoServletSessionException ;
	 Simulator getNewSimulator(String actorTypeName, SimId simId) throws Exception;
	 List<SimulatorConfig> getSimConfigs(List<SimId> ids) throws Exception;
	List<SimulatorConfig> getAllSimConfigs(GetAllSimConfigsRequest user) throws Exception;
	 String putSimConfig(SimulatorConfig config) throws Exception;
	 String deleteConfig(SimulatorConfig config) throws Exception;
	 Map<String, SimId> getActorSimulatorNameMap() throws NoServletSessionException;
	//	 List<String> getSimulatorTransactionNames(String simid) throws Exception;
	 int removeOldSimulators() throws NoServletSessionException;
	List<SimulatorStats> getSimulatorStats(List<SimId> simid) throws Exception;
	List<Pid> getPatientIds(SimId simId) throws Exception;
	String addPatientIds(SimId simId, List<Pid> pids) throws Exception;
	boolean deletePatientIds(SimId simId, List<Pid> pids) throws Exception;
	Result getSimulatorEventRequest(TransactionInstance ti) throws Exception;
	Result getSimulatorEventResponse(TransactionInstance ti) throws Exception;

	String setToolkitProperties(Map<String, String> props) throws Exception;
	Map<String, String> getToolkitProperties() throws NoServletSessionException ;
	boolean reloadPropertyFile() throws NoServletSessionException ;

	String getTransactionRequest(SimId simName, String actor, String trans, String event)  throws NoServletSessionException;
	String getTransactionResponse(SimId simName, String actor, String trans, String event)  throws NoServletSessionException;
	String getTransactionLog(SimId simName, String actor, String trans, String event)  throws NoServletSessionException;
	List<String> getTransactionsForSimulator(SimId simName) throws Exception;
	MessageValidationResults executeSimMessage(String simFileSpec) throws NoServletSessionException;

	void renameSimFile(String simFileSpec, String newSimFileSpec) throws Exception;
	void deleteSimFile(String simFileSpec) throws Exception;
	String getSimulatorEndpoint() throws NoServletSessionException;
	List<Result> getSelectedMessage(String simFilename) throws NoServletSessionException;
	List<Result> getSelectedMessageResponse(String simFilename) throws NoServletSessionException;
	@Deprecated
	String getClientIPAddress();

	List<TransactionInstance> getTransInstances(SimId simid, String actor, String trans)  throws Exception;

	List<Result> getLastMetadata();
	String getLastFilename();
	String getTimeAndDate();

	MessageValidationResults validateMessage(ValidationContext vc) throws Exception;

	List<String> getSiteNames(GetSiteNamesRequest request) throws Exception;
	List<String> getRegistryNames() throws Exception;
	List<String> getRepositoryNames() throws Exception;
	List<String> getRGNames() throws NoServletSessionException ;
	List<String> getIGNames() throws NoServletSessionException ;
	List<String> getTestdataSetListing(String environmentName, String sessionName,String testdataSetName)  throws NoServletSessionException;
	CodesResult getCodesConfiguration(String getCodesConfiguration) throws NoServletSessionException ;
	TransactionOfferings getTransactionOfferings(CommandContext commandContext) throws Exception;

	List<String> reloadSites(boolean simAlso) throws Exception;
	List<String> reloadExternalSites() throws Exception;
	Site getSite(String siteName) throws Exception;
	Collection<Site> getAllSites(CommandContext commandContext) throws Exception;
	String saveSite(SaveSiteRequest request) throws Exception;
	String deleteSite(String siteName) throws Exception;

	List<Result> getSSandContents(SiteSpec site, String ssuid,  Map<String, List<String>> codeSpec) throws NoServletSessionException ;
	List<Result> srcStoresDocVal(SiteSpec site, String ssuid) throws NoServletSessionException ;
	List<Result> findDocuments(SiteSpec site, String pid, boolean onDemand) throws NoServletSessionException ;
	List<Result> findDocumentsByRefId(SiteSpec site, String pid, List<String> refIds) throws NoServletSessionException ;
	List<Result> findFolders(SiteSpec site, String pid) throws NoServletSessionException ;
	// FIXME this method has far too many parameters we need to change that by one object.
	List<Result> findPatient(SiteSpec site, String firstName,
							 String secondName, String lastName, String suffix, String gender,
							 String dob, String ssn, String pid, String homeAddress1,
							 String homeAddress2, String homeCity, String homeState,
							 String homeZip, String homeCountry, String mothersFirstName, String mothersSecondName,
							 String mothersLastName, String mothersSuffix, String homePhone,
							 String workPhone, String principleCareProvider, String pob,
							 String pobAddress1, String pobAddress2, String pobCity, String country,
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
	List<Result> retrieveImagingDocSet(SiteSpec site, Uids uids, String studyRequest, String transferSyntax) throws Exception;
	List<Result> submitRegistryTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException ;
	List<Result> submitRepositoryTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException ;
	List<Result> submitXDRTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException ;
	List<Result> provideAndRetrieve(SiteSpec site, String pid) throws NoServletSessionException ;
	List<Result> lifecycleValidation(SiteSpec site, String pid) throws NoServletSessionException ;
	List<Result> folderValidation(SiteSpec site, String pid) throws NoServletSessionException ;

	List<Result> mpqFindDocuments(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) throws NoServletSessionException;
	List<Result> getAll(SiteSpec site, String pid, Map<String, List<String>> codesSpec) throws NoServletSessionException;
	List<Result> findDocuments2(SiteSpec site, String pid, Map<String, List<String>> codesSpec) throws NoServletSessionException;

	TestLogs getRawLogs(TestInstance logId) throws NoServletSessionException ;

	String getAdminPassword() throws NoServletSessionException ;

	String getTestplanAsText(String testSession,TestInstance testInstance, String section) throws Exception;
	TestPartFileDTO getSectionTestPartFile(String testSession, TestInstance testInstance, String section) throws Exception;
	TestPartFileDTO loadTestPartContent(TestPartFileDTO testPartFileDTO) throws Exception;
	String getHtmlizedString(String xml) throws Exception;

	String getImplementationVersion() throws NoServletSessionException ;

	List<String> getUpdateNames() throws NoServletSessionException ;
	List<TestInstance> getTestlogListing(String sessionName) throws Exception;
	List<TestOverviewDTO> getTestsOverview(String sessionName, List<TestInstance> testInstances) throws Exception;

	List<RegistryStatus> getDashboardRegistryData() throws Exception;
	List<RepositoryStatus> getDashboardRepositoryData() throws Exception;

	List<String> getSiteNamesWithRG() throws Exception;
	List<String> getSiteNamesWithRIG() throws Exception;
	List<String> getSiteNamesWithIDS() throws Exception;
	List<String> getSiteNamesByTranType(String transactionType) throws Exception;

	String reloadSystemFromGazelle(String systemName) throws Exception;
	boolean isGazelleConfigFeedEnabled() throws NoServletSessionException ;
	List<String> getEnvironmentNames(CommandContext context) throws Exception;
	String setEnvironment(CommandContext context) throws Exception;
	String getCurrentEnvironment() throws NoServletSessionException;
	String getDefaultEnvironment(CommandContext context) throws Exception;
	String getDefaultAssigningAuthority(CommandContext context) throws Exception;
	String getAttributeValue(String username, String attName) throws Exception;
	void setAttributeValue(String username, String attName, String attValue) throws Exception;
	RawResponse buildIgTestOrchestration(IgOrchestrationRequest request);
	RawResponse buildIigTestOrchestration(IigOrchestrationRequest request);
	RawResponse buildRigTestOrchestration(RigOrchestrationRequest request);
	RawResponse buildRgTestOrchestration(RgOrchestrationRequest request);
	RawResponse buildIdsTestOrchestration(IdsOrchestrationRequest request);
	RawResponse buildRepTestOrchestration(RepOrchestrationRequest request);
	RawResponse buildRegTestOrchestration(RegOrchestrationRequest request);
	RawResponse buildRSNAEdgeTestOrchestration(RSNAEdgeOrchestrationRequest request);

	Map<String, String> getSessionProperties() throws NoServletSessionException;
	void setSessionProperties(Map<String, String> props) throws NoServletSessionException;

	List<Pid> retrieveConfiguredFavoritesPid(CommandContext commandContext) throws Exception;
	Pid createPid(GeneratePidRequest generatePidRequest) throws Exception;
	String getAssigningAuthority(CommandContext commandContext) throws Exception;
	List<String> getAssigningAuthorities(CommandContext commandContext) throws Exception;
	List<Result> sendPidToRegistry(SendPidToRegistryRequest request) throws Exception;

	/**
	 * This method copy the default testkit to a selected environment and triggers a code update based on
	 * the affinity domain configuration file (codes.xml) located in the selected environment.
	 * @param context Environment name of the target environment selected for the testkit.
	 * @return update output as a String
	 */
	String configureTestkit(CommandContext context) throws Exception;

	/**
	 * This method tests if there already is a testkit configured in a selected environment.
	 * @param context name of the selected environment.
	 * @return boolean
	 */
	boolean doesTestkitExist(CommandContext context) throws Exception;


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Tests Overview Tab
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	 List<Test> reloadAllTestResults(String sessionName) throws Exception;
	 List<Test> runAllTests(Site site) throws NoServletSessionException;
	 List<Test> deleteAllTestResults(Site site) throws NoServletSessionException;
	 Test runSingleTest(Site site, int testId) throws NoServletSessionException;
	 TestOverviewDTO deleteSingleTestResult(String testSession, TestInstance testInstance) throws Exception;

	String setMesaTestSession(String sessionName) throws NoServletSessionException ;
	String getNewPatientId(String assigningAuthority) throws NoServletSessionException ;
	List<String> getTransactionErrorCodeRefs(String transactionName, Severity severity) throws Exception;

	String getServletContextName();

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Background test plan running methods related to On-Demand Documents
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	 Result register(String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params) throws Exception;
	 Map<String, String> registerWithLocalizedTrackingInODDS(String username, TestInstance testInstance, SiteSpec registry, SimId oddsSimId, Map<String, String> params) throws Exception;
	 List<DocumentEntryDetail> getOnDemandDocumentEntryDetails(SimId oddsSimId);

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Interaction methods
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	 InteractingEntity getInteractionFromModel(InteractingEntity model) throws Exception;

	String clearTestSession(CommandContext context) throws Exception;

	boolean getAutoInitConformanceTesting();

	boolean indexTestKits();
}
