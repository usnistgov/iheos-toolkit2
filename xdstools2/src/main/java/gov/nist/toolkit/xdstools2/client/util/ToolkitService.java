package gov.nist.toolkit.xdstools2.client.util;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.Test;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.services.client.IdcOrchestrationRequest;
import gov.nist.toolkit.services.client.PifType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.session.client.ConformanceSessionValidationStatus;
import gov.nist.toolkit.session.client.TestSessionStats;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.UserTestCollection;
import gov.nist.toolkit.xdstools2.shared.NoServletSessionException;
import gov.nist.toolkit.xdstools2.shared.RegistryStatus;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.InitializationResponse;
import gov.nist.toolkit.xdstools2.shared.command.request.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@RemoteServiceRelativePath("toolkit")
public interface ToolkitService extends RemoteService  {

	TkProps getTkProps() throws NoServletSessionException;

	ConformanceSessionValidationStatus validateConformanceSession(String testSession, String siteName) throws Exception;
	Collection<String> getSitesForTestSession(CommandContext context) throws Exception;
	InitializationResponse getInitialization(CommandContext context) throws Exception;
	String getAssignedSiteForTestSession(CommandContext context) throws Exception;
	void setAssignedSiteForTestSession(SetAssignedSiteForTestSessionRequest request) throws Exception;


	/* Test management */
	Map<String, Result> getTestResults(GetTestResultsRequest resultsRequest) throws Exception;
	LogFileContentDTO getTestLogDetails(GetTestLogDetailsRequest request) throws Exception;

    Map<String, String> getCollectionNames(GetCollectionRequest request) throws Exception;

    List<TestInstance> getCollectionMembers(GetCollectionRequest request) throws Exception;

    List<TestCollectionDefinitionDAO> getTestCollections(GetCollectionRequest request) throws Exception;

    Map<String, String> getCollection(GetCollectionRequest request) throws Exception;

    String getTestReadme(GetTestDetailsRequest request) throws Exception;

    List<String> getTestIndex(GetTestDetailsRequest request) throws Exception;

    List<TestSessionStats> getTestSessionStats(CommandContext commandContext) throws Exception;

    List<Result> runMesaTest(RunTestRequest request) throws Exception;
	TestOverviewDTO runTest(RunTestRequest request) throws Exception;
	boolean isPrivateMesaTesting() throws NoServletSessionException;

	boolean testSessionExists(CommandContext request) throws Exception;
	TestSession buildTestSession() throws Exception;

	List<String> getTestSessionNames(CommandContext request) throws Exception;
	boolean addTestSession(CommandContext context) throws Exception;
	boolean deleteTestSession(CommandContext context) throws Exception;

//	boolean isAdminPasswordValid(IsAdminPasswordValidRequest request) throws Exception;
	String getAdminPasswordHash(GetAdminPasswordHashRequest request) throws Exception;

	/* Simulator Management */
	List<String> getActorTypeNames(CommandContext context) throws Exception;
	Simulator getNewSimulator(GetNewSimulatorRequest request) throws Exception;
	List<SimulatorConfig> getSimConfigs(GetSimConfigsRequest request) throws Exception;
	List<SimulatorConfig> getAllSimConfigs(GetAllSimConfigsRequest user) throws Exception;
	String putSimConfig(SimConfigRequest request) throws Exception;
	String deleteConfig(SimConfigRequest request) throws Exception;
	List<SimId> getSimIdsForUser(GetSimIdsForUserRequest context) throws Exception;
	//	 List<String> getSimulatorTransactionNames(String simid) throws Exception;
	int removeOldSimulators(CommandContext context) throws Exception;
	List<SimulatorStats> getSimulatorStats(GetSimulatorStatsRequest request) throws Exception;
	List<Pid> getPatientIds(PatientIdsRequest request) throws Exception;
	String addPatientIds(PatientIdsRequest request) throws Exception;
	boolean deletePatientIds(PatientIdsRequest request) throws Exception;
	Result getSimulatorEventRequest(GetSimulatorEventRequest request) throws Exception;
	Result getSimulatorEventResponse(GetSimulatorEventRequest request) throws Exception;

	String setToolkitProperties(SetToolkitPropertiesRequest request) throws Exception;
	Map<String, String> getToolkitProperties(CommandContext context) throws Exception;
	Map<String, String> getAdminToolkitProperties(GetAdminToolkitPropertiesRequest request) throws Exception;
	boolean reloadPropertyFile() throws NoServletSessionException;

	Map<String, String> getOrchestrationProperties(GetOrchestrationPropertiesRequest request) throws Exception;
	PifType getOrchestrationPifType(GetOrchestrationPifTypeRequest request) throws Exception;

	Message getTransactionRequest(GetTransactionRequest request) throws Exception;
	Message getTransactionResponse(GetTransactionRequest request) throws Exception;
	String getTransactionLog(GetTransactionRequest request)  throws Exception;
	TransactionInstance getTransactionLogDirectoryPath(GetTransactionLogDirectoryPathRequest request) throws Exception;
	List<InteractingEntity> setSutInitiatedTransactionInstance(SetSutInitiatedTransactionInstanceRequest request) throws Exception;
	List<String> getTransactionsForSimulator(GetTransactionRequest request) throws Exception;
	MessageValidationResults executeSimMessage(ExecuteSimMessageRequest request) throws Exception;

	void renameSimFile(RenameSimFileRequest request) throws Exception;
	void deleteSimFile(DeleteSimFileRequest request) throws Exception;
	String getSimulatorEndpoint(CommandContext context) throws Exception;
	List<Result> getSelectedMessage(GetSelectedMessageRequest request) throws Exception;
	List<Result> getSelectedMessageResponse(GetSelectedMessageRequest request) throws Exception;
	@Deprecated
	String getClientIPAddress();

	List<TransactionInstance> getTransInstances(GetTransactionRequest request)  throws Exception;
	List<List<TransactionInstance>> getTransInstancesLists(GetTransactionListsRequest request)  throws Exception;

	List<Result> getLastMetadata(CommandContext context) throws Exception;
	String getLastFilename(CommandContext context) throws Exception;
	String getTimeAndDate(CommandContext context) throws Exception;

    SimId getFullSimId(GetFullSimIdRequest request) throws Exception;

    Result updateDocumentEntry(UpdateDocumentEntryRequest request) throws Exception;
    MessageValidationResults validateDocumentEntry(ValidateDocumentEntryRequest request) throws Exception;

    MessageValidationResults validateMessage(ValidateMessageRequest request) throws Exception;

	List<String> getSiteNames(GetSiteNamesRequest request) throws Exception;
	List<String> getRegistryNames() throws Exception;
	List<String> getRepositoryNames() throws Exception;
	List<String> getRGNames() throws NoServletSessionException;
	List<String> getIGNames() throws NoServletSessionException;
	List<String> getTestdataSetListing(GetTestdataSetListingRequest request)  throws Exception;
	CodesResult getCodesConfiguration(CommandContext context) throws Exception;
	TransactionOfferings getTransactionOfferings(CommandContext commandContext) throws Exception;

	List<String> reloadSites(boolean simAlso) throws Exception;
	List<String> reloadExternalSites(CommandContext context) throws Exception;
	Site getSite(GetSiteRequest request) throws Exception;
	Collection<Site> getAllSites(CommandContext commandContext) throws Exception;
	String saveSite(SaveSiteRequest request) throws Exception;
	String deleteSite(DeleteSiteRequest request) throws Exception;

	List<Result> getSSandContents(GetSubmissionSetAndContentsRequest request) throws Exception;
	List<Result> srcStoresDocVal(GetSrcStoresDocValRequest request) throws Exception;
	List<Result> findDocuments(FindDocumentsRequest request) throws Exception;
	List<Result> findDocumentsByRefId(FindDocumentsRequest request) throws Exception;
	List<Result> findFolders(FoldersRequest request) throws Exception;
	// FIXME this method has far too many parameters we need to change that by one object.
	List<Result> getDocuments(GetDocumentsRequest request) throws Exception;
	List<Result> getFolders(GetFoldersRequest request) throws Exception;
	List<Result> getFoldersForDocument(GetFoldersRequest request) throws Exception;
	List<Result> getFolderAndContents(GetFoldersRequest request) throws Exception;
	List<Result> getObjects(GetObjectsRequest request) throws Exception;
	List<Result> getAssociations(GetAssociationsRequest request) throws Exception;
	List<Result> getSubmissionSets(GetSubmissionSetsRequest request) throws Exception;
	List<Result> registerAndQuery(RegisterAndQueryRequest request) throws Exception;
	List<Result> getRelated(GetRelatedRequest request) throws Exception;
	List<Result> retrieveDocument(RetrieveDocumentRequest request) throws Exception;
	List<Result> retrieveImagingDocSet(RetrieveImagingDocSetRequest request) throws Exception;
	List<Result> submitRegistryTestdata(SubmitTestdataRequest request) throws Exception;
	List<Result> submitRepositoryTestdata(SubmitTestdataRequest request) throws Exception;
	List<Result> submitXDRTestdata(SubmitTestdataRequest request) throws Exception;
	List<Result> provideAndRetrieve(ProvideAndRetrieveRequest request) throws Exception;
	List<Result> lifecycleValidation(LifecycleValidationRequest request) throws Exception;
	List<Result> folderValidation(FoldersRequest request) throws Exception;

	List<Result> mpqFindDocuments(MpqFindDocumentsRequest request) throws Exception;
	List<Result> getAll(GetAllRequest request) throws Exception;
	List<Result> findDocuments2(FindDocuments2Request request) throws Exception;

	TestLogs getRawLogs(GetRawLogsRequest request) throws Exception;
//	List<Message> getFhirResult(GetRawLogsRequest request) throws Exception;

	String getTestplanAsText(GetTestplanAsTextRequest request) throws Exception;
	TestPartFileDTO getSectionTestPartFile(GetSectionTestPartFileRequest request) throws Exception;
	TestPartFileDTO loadTestPartContent(LoadTestPartContentRequest request) throws Exception;
	String getHtmlizedString(String xml) throws Exception;

	String getImplementationVersion(CommandContext context) throws Exception;

	List<String> getUpdateNames() throws NoServletSessionException;
	List<TestInstance> getTestlogListing(String sessionName) throws Exception;
	List<TestOverviewDTO> getTestsOverview(GetTestsOverviewRequest request) throws Exception;
	List<TestOverviewDTO> getActorTestProgress(GetTestsOverviewRequest request) throws Exception;
	List<SectionDefinitionDAO> getTestSectionsDAOs(GetTestSectionsDAOsRequest request) throws Exception;
	List<RegistryStatus> getDashboardRegistryData(CommandContext context) throws Exception;
	List<RepositoryStatus> getDashboardRepositoryData(CommandContext context) throws Exception;

	List<String> getSiteNamesWithRG(CommandContext context) throws Exception;
	List<String> getSiteNamesWithRepository(CommandContext context) throws Exception;
	List<String> getSiteNamesWithRIG(CommandContext context) throws Exception;
	List<String> getSiteNamesWithIDS(CommandContext context) throws Exception;
	List<String> getSiteNamesByTranType(GetSiteNamesByTranTypeRequest request) throws Exception;

	String reloadSystemFromGazelle(ReloadSystemFromGazelleRequest request) throws Exception;
	boolean isGazelleConfigFeedEnabled(CommandContext context) throws Exception;
	List<String> getEnvironmentNames(CommandContext context) throws Exception;
	String setEnvironment(CommandContext context) throws Exception;
	String getCurrentEnvironment() throws NoServletSessionException;
	String getDefaultEnvironment(CommandContext context) throws Exception;
	String getDefaultAssigningAuthority(CommandContext context) throws Exception;
	String getAttributeValue(String username, String attName) throws Exception;
	void setAttributeValue(String username, String attName, String attValue) throws Exception;
	RawResponse buildIgTestOrchestration(BuildIgTestOrchestrationRequest request) throws Exception;
	RawResponse buildIigTestOrchestration(BuildIigTestOrchestrationRequest request)throws Exception;
	RawResponse buildRigTestOrchestration(BuildRigTestOrchestrationRequest request) throws Exception;
	RawResponse buildRgTestOrchestration(BuildRgTestOrchestrationRequest request) throws Exception;
	RawResponse buildIdsTestOrchestration(BuildIdsTestOrchestrationRequest request) throws Exception;
	RawResponse buildRepTestOrchestration(BuildRepTestOrchestrationRequest request) throws Exception;
	RawResponse buildRegTestOrchestration(BuildRegTestOrchestrationRequest request) throws Exception;
	RawResponse buildRSNAEdgeTestOrchestration(BuildRSNAEdgeTestOrchestrationRequest request) throws Exception;
    RawResponse buildIdcTestOrchestration(IdcOrchestrationRequest request);
	RawResponse buildEsTestOrchestration(BuildEsTestOrchestrationRequest request);

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

	/**
	 * This method creates the testkits folder structure in the EC for all available environments.
	 * @param context
	 */
	void generateTestkitStructure(CommandContext context) /*throws Exception*/;


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Tests Overview Tab
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	List<Test> reloadAllTestResults(CommandContext context) throws Exception;
	List<Test> runAllTests(AllTestRequest request) throws Exception;
	List<Test> deleteAllTestResults(AllTestRequest request) throws Exception;
	Test runSingleTest(RunSingleTestRequest request) throws Exception;
	TestOverviewDTO deleteSingleTestResult(DeleteSingleTestRequest request) throws Exception;
	List<TestOverviewDTO> deleteMultipleTestLogs(DeleteMultipleTestLogsRequest request) throws Exception;

	String setTestSession(String sessionName) throws NoServletSessionException ;
	List<String> getTransactionErrorCodeRefs(GetTransactionErrorCodeRefsRequest refsRequest) throws Exception;

	String getServletContextName();

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Background test plan running methods related to On-Demand Documents
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	Result register(RegisterRequest registerRequest) throws Exception;
	Map<String, String> registerWithLocalizedTrackingInODDS(RegisterRequest registerRequest) throws Exception;
	List<DocumentEntryDetail> getOnDemandDocumentEntryDetails(GetOnDemandDocumentEntryDetailsRequest request) throws Exception;
	boolean setOdSupplyStateIndex(SetOdSupplyStateIndexRequest request) throws Exception;

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Interaction methods
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	InteractingEntity getInteractionFromModel(GetInteractionFromModelRequest request) throws Exception;

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// STS SAML
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	String getStsSamlAssertion(GetStsSamlAssertionRequest request) throws Exception;
    Map<String,String> getStsSamlAssertionsMap(GetStsSamlAssertionMapRequest request) throws Exception;

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Tab config
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	TabConfig getToolTabConfig(GetTabConfigRequest request) throws Exception;
	UserTestCollection getPrunedToolTabConfig(GetTabConfigRequest request) throws Exception;

	String clearTestSession(CommandContext context) throws Exception;

	boolean getAutoInitConformanceTesting(CommandContext context);

	boolean indexTestKits(CommandContext context);

    RawResponse buildRecTestOrchestration(BuildRecTestOrchestrationRequest request) throws Exception;


	List<DatasetModel> getAllDatasets(CommandContext context) throws Exception;

//    List<Result> fhirCreate(FhirCreateRequest request) throws Exception;

//	List<Result> fhirTransaction(FhirTransactionRequest request) throws Exception;

    String getDatasetContent(GetDatasetElementContentRequest var1);

	RawResponse buildDocAdminTestOrchestration(BuildDocAdminTestOrchestrationRequest request) throws Exception;
    RawResponse buildSrcTestOrchestration(BuildSrcTestOrchestrationRequest request) throws Exception;
	RawResponse buildIsrTestOrchestration(BuildIsrTestOrchestrationRequest request) throws Exception;

//    RawResponse buildFhirSupportOrchestration(FhirSupportOrchestrationRequest var1) throws Exception;

//    List<Result> fhirSearch(FhirSearchRequest var1) throws Exception;

//    List<Result> fhirRead(FhirReadRequest request) throws Exception;

    String promote(PromoteRequest request);

    MetadataCollection getMetadataFromRegIndex(GetMetadataFromRegIndexRequest request) throws Exception;

    boolean reloadToolkitLogging(CommandContext context) throws Exception;
}
