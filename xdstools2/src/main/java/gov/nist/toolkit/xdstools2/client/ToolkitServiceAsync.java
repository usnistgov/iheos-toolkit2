package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
import gov.nist.toolkit.xdstools2.client.command.CommandContext;
import gov.nist.toolkit.xdstools2.client.command.request.GeneratePidRequest;
import gov.nist.toolkit.xdstools2.client.command.request.GetAllSimConfigsRequest;
import gov.nist.toolkit.xdstools2.client.command.request.SendPidToRegistryRequest;
import gov.nist.toolkit.xdstools2.client.command.response.InitializationResponse;
import gov.nist.toolkit.session.client.ConformanceSessionValidationStatus;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ToolkitServiceAsync {

	void clearTestSession(String testSession, AsyncCallback<String> callback);
	void validateConformanceSession(String testSession, String siteName, AsyncCallback<ConformanceSessionValidationStatus> callback);
	void getSitesForTestSession(String testSession, AsyncCallback<Collection<String>> callback);
	void getInitialization(AsyncCallback<InitializationResponse> callback);
	void getTkProps(AsyncCallback<TkProps> callback);
	void getSessionProperties(AsyncCallback<Map<String, String>> callback);
	void setSessionProperties(Map<String, String> props, AsyncCallback callback);
	void getNewPatientId(String assigningAuthority, AsyncCallback<String> callback);
	
	void getDefaultAssigningAuthority(AsyncCallback<String> callback);
	void getAttributeValue(String username, String attName, AsyncCallback<String> callback);
	void setAttributeValue(String username, String attName, String attValue, AsyncCallback callback);


	void getCurrentEnvironment(AsyncCallback<String> callback);
	void getDefaultEnvironment(AsyncCallback<String> callback);
	void setEnvironment(String name, AsyncCallback callback);
	void getEnvironmentNames(CommandContext context, AsyncCallback<List<String>> callback);
	void isGazelleConfigFeedEnabled(AsyncCallback<Boolean> callback);
	void reloadSystemFromGazelle(String systemName, AsyncCallback<String> callback);
	void getSiteNamesWithRG(AsyncCallback<List<String>> callback);
	void getSiteNamesByTranType(String transactionType, AsyncCallback<List<String>> callback);

	void getDashboardRegistryData(AsyncCallback<List<RegistryStatus>> callback);
	void getDashboardRepositoryData(AsyncCallback<List<RepositoryStatus>> callback);

	void getTestsOverview(String sessionName, List<TestInstance> testInstances, AsyncCallback<List<TestOverviewDTO>> callback);
	void getUpdateNames(AsyncCallback<List<String>> callback);
	
	void getTransactionRequest(SimId simName, String actor, String trans, String event, AsyncCallback<String> callback);
	void getTransactionResponse(SimId simName, String actor, String trans, String event, AsyncCallback<String> callback);
	void getTransactionLog(SimId simName, String actor, String trans, String event, AsyncCallback<String> callback);

	void getTransactionsForSimulator(SimId simid, AsyncCallback<List<String>> callback);

//	void getActorNames(AsyncCallback<List<String>> notify);

	void executeSimMessage(String simFileSpec, AsyncCallback<MessageValidationResults> callback);

	
	void renameSimFile(String simFileSpec, String newSimFileSpec, AsyncCallback callback);

	void deleteSimFile(String simFileSpec, AsyncCallback callback);

	void getSimulatorEndpoint(AsyncCallback<String> callback);

	void getSelectedMessage(String simFilename, AsyncCallback<List<Result>> callback);
	void getSelectedMessageResponse(String simFilename, AsyncCallback<List<Result>> callback);
	@Deprecated
	void getClientIPAddress(AsyncCallback<String> callback);

//	void  validateMessage(ValidationContext vc, String simFileName, AsyncCallback<MessageValidationResults> notify);

	void  getTransInstances(SimId simid, String actor, String trans, AsyncCallback<List<TransactionInstance>> callback);
  
	void getLastMetadata(AsyncCallback<List<Result>> callback);
	void getLastFilename(AsyncCallback<String> callback);
	void getTimeAndDate(AsyncCallback<String> callback);
	
	void validateMessage(ValidationContext vc, AsyncCallback<MessageValidationResults> callback);
	
	void getSiteNames(boolean reload, boolean simAlso, AsyncCallback<List<String>> callback);

	void getTransactionOfferings(CommandContext commandContext, AsyncCallback<TransactionOfferings> callback);
	void getRegistryNames(AsyncCallback<List<String>> callback);
	void getRepositoryNames(AsyncCallback<List<String>> callback);
	void getRGNames(AsyncCallback<List<String>> callback);
	void getIGNames(AsyncCallback<List<String>> callback);
	void getRawLogs(TestInstance logId, AsyncCallback<TestLogs> callback);
	void getTestdataSetListing(String environmentName, String sessionName, String testdataSetName, AsyncCallback<List<String>> callback);
	void getCodesConfiguration(String getCodesConfiguration, AsyncCallback<CodesResult> callback);
	void getSite(String siteName, AsyncCallback<Site> callback);
	void getAllSites(CommandContext commandContext, AsyncCallback<Collection<Site>> callback);
	void saveSite(Site site, AsyncCallback<String> callback);
	void reloadSites(boolean simAlso, AsyncCallback<List<String>> callback);
	void reloadExternalSites(AsyncCallback<List<String>> callback);
	void deleteSite(String siteName, AsyncCallback<String> callback);

	void getSSandContents(SiteSpec site, String ssuid, Map<String, List<String>> codeSpec, AsyncCallback<List<Result>> callback);
	void srcStoresDocVal(SiteSpec site, String ssuid, AsyncCallback<List<Result>> callback);
	void findDocuments(SiteSpec site, String pid, boolean onDemand, AsyncCallback<List<Result>> callback);
	void findDocumentsByRefId(SiteSpec site, String pid, List<String> refIds, AsyncCallback<List<Result>> callback) ;
	void findFolders(SiteSpec site, String pid, AsyncCallback<List<Result>> callback);
	void findPatient(SiteSpec site, String firstName, String secondName, String lastName, String suffix,
			String gender, String dob, String ssn, String pid,
			String homeAddress1, String homeAddress2, String homeCity, String homeState, String homeZip, String homeCountry,
            String mothersFirstName, String mothersSecondName, String mothersLastName, String mothersSuffix, 
            String homePhone, String workPhone, String principleCareProvider, 
            String pob, String pobAddress1, String pobAddress2, String pobCity, String pobState, String pobZip, String pobCountry,
            AsyncCallback<List<Result>> callback);
	void getDocuments(SiteSpec site, AnyIds ids, AsyncCallback<List<Result>> callback);
	void getFolders(SiteSpec site, AnyIds aids, AsyncCallback<List<Result>> callback);
	void getFoldersForDocument(SiteSpec site, AnyIds aids, AsyncCallback<List<Result>> callback);
	void getFolderAndContents(SiteSpec site, AnyIds aids, AsyncCallback<List<Result>> callback);
	void getObjects(SiteSpec site, ObjectRefs ids, AsyncCallback<List<Result>> callback);
	void getAssociations(SiteSpec site, ObjectRefs ids, AsyncCallback<List<Result>> callback);
	void getSubmissionSets(SiteSpec site, AnyIds ids, AsyncCallback<List<Result>> callback);
	void registerAndQuery(SiteSpec site, String pid, AsyncCallback<List<Result>> callback);
	void getRelated(SiteSpec site, ObjectRef or, List<String> assocs, AsyncCallback<List<Result>> callback);
	void retrieveDocument(SiteSpec site, Uids uids, AsyncCallback<List<Result>> callback);
	void retrieveImagingDocSet(SiteSpec site, Uids uids, String studyRequest, String transferSyntax, AsyncCallback<List<Result>> callback);
	void submitRegistryTestdata(String testSessionName, SiteSpec site, String datasetName, String pid, AsyncCallback<List<Result>> callback);
	void submitRepositoryTestdata(String testSessionName, SiteSpec site, String datasetName, String pid, AsyncCallback<List<Result>> callback);
	void submitXDRTestdata(String testSessionName, SiteSpec site, String datasetName, String pid, AsyncCallback<List<Result>> callback);
	void provideAndRetrieve(SiteSpec site, String pid, AsyncCallback<List<Result>> callback);
	void lifecycleValidation(SiteSpec site, String pid, AsyncCallback<List<Result>> callback);
	void folderValidation(SiteSpec site, String pid, AsyncCallback<List<Result>> callback);

//	void mpqFindDocuments(SiteSpec site, String pid, List<String> classCodes, List<String> hcftCodes, List<String> eventCodes, AsyncCallback<List<Result>> notify);
	void mpqFindDocuments(SiteSpec site, String pid, Map<String, List<String>> selectedCodes, AsyncCallback<List<Result>> callback);
	void getAll(SiteSpec site, String pid, Map<String, List<String>> codesSpec, AsyncCallback<List<Result>> callback);
	void findDocuments2(SiteSpec site, String pid, Map<String, List<String>> codesSpec, AsyncCallback<List<Result>> callback);

	void getAdminPassword(AsyncCallback<String> callback);
	
	void getImplementationVersion(AsyncCallback<String> callback);
	 
	void setToolkitProperties(Map<String, String> props, AsyncCallback<String> callback);
	void getToolkitProperties(AsyncCallback<Map<String, String>> callback);
	void reloadPropertyFile(AsyncCallback<Boolean> callback);
	
	void  getActorTypeNames(AsyncCallback<List<String>> callback);
	void  getNewSimulator(String actorTypeName, SimId simId, AsyncCallback<Simulator> callback);
	void getSimConfigs(List<SimId> ids, AsyncCallback<List<SimulatorConfig>> callback);
	void getAllSimConfigs(GetAllSimConfigsRequest user, AsyncCallback<List<SimulatorConfig>> callback);
	void putSimConfig(SimulatorConfig config, AsyncCallback<String> callback);
	void deleteConfig(SimulatorConfig config, AsyncCallback<String> callback);
	void getActorSimulatorNameMap(AsyncCallback<Map<String, SimId>> callback);
//	void getSimulatorTransactionNames(String simid, AsyncCallback<List<String>> notify);
	void removeOldSimulators(AsyncCallback<Integer> callback);
	void getSimulatorStats(List<SimId> simid, AsyncCallback<List<SimulatorStats>> callback) throws Exception;
	void getPatientIds(SimId simId, AsyncCallback<List<Pid>> callback) throws Exception;
	void addPatientIds(SimId simId, List<Pid> pids, AsyncCallback<String> callback) throws Exception;
	void deletePatientIds(SimId simId, List<Pid> pids, AsyncCallback<Boolean> callback) throws Exception;

	void getCollectionNames(String collectionSetName, AsyncCallback<Map<String, String>> callback);
	void getCollectionMembers(String collectionSetName, String collectionName, AsyncCallback<List<String>> callback);
	void getTestCollections(String collectionSetName, AsyncCallback<List<TestCollectionDefinitionDAO>> callback);
	void getCollection(String collectionSetName, String collectionName, AsyncCallback<Map<String, String>> callback);
	void getTestReadme(String test, AsyncCallback<String> callback);
	void getTestIndex(String test, AsyncCallback<List<String>> callback);
	void runMesaTest(String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure, AsyncCallback<List<Result>> callback);
	void runTest(String environment, String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, Map<String, String> params, boolean stopOnFirstFailure, AsyncCallback<TestOverviewDTO> callback) throws NoServletSessionException;
	void isPrivateMesaTesting(AsyncCallback<Boolean> callback);
	void addMesaTestSession(String name, AsyncCallback<Boolean> callback);
	void delMesaTestSession(String name, AsyncCallback<Boolean> callback);
	void createPid(GeneratePidRequest generatePidRequest, AsyncCallback<Pid> callback);
	void getAssigningAuthority(CommandContext commandContext, AsyncCallback<String> callback);
	void getAssigningAuthorities(CommandContext commandContext, AsyncCallback<List<String>> callback);
	void sendPidToRegistry(SendPidToRegistryRequest request, AsyncCallback<List<Result>> callback);
	void getSimulatorEventRequest(TransactionInstance ti, AsyncCallback<Result> callback) throws Exception;
	void getSimulatorEventResponse(TransactionInstance ti, AsyncCallback<Result> callback) throws Exception;
	void getTestLogDetails(String sessionName, TestInstance testInstance, AsyncCallback<LogFileContentDTO> callback);

	void getTestplanAsText(String testSession, TestInstance testInstance, String section, AsyncCallback<String> callback);
	void getSectionTestPartFile(String testSession, TestInstance testInstance, String section, AsyncCallback<TestPartFileDTO> callback);
	void getHtmlizedString(String xml, AsyncCallback<String> callback);

	void configureTestkit(String selectedEnvironment,AsyncCallback<String> callback);

	void doesTestkitExist(String selectedEnvironment, AsyncCallback<Boolean> asyncCallback);

//	void getToolkitEnableNwHIN(AsyncCallback<String> notify);

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Test Services
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	void reloadAllTestResults(String sessionName, AsyncCallback<List<Test>> callback) throws Exception;
	void getTestlogListing(String sessionName, AsyncCallback<List<TestInstance>> callback);
	void getTestResults(List<TestInstance> testIds, String testSession, AsyncCallback<Map<String, Result>> callback);
	void setMesaTestSession(String sessionName, AsyncCallback callback);
	void getMesaTestSessionNames(CommandContext request, AsyncCallback<List<String>> callback);
	void deleteAllTestResults(Site site, AsyncCallback<List<Test>> callback);
	void deleteSingleTestResult(String testSession, TestInstance testInstance, AsyncCallback<TestOverviewDTO> callback);
	void runAllTests(Site site, AsyncCallback<List<Test>> callback);
	void runSingleTest(Site site, int testId, AsyncCallback<Test> callback);
    void getTransactionErrorCodeRefs(String transactionName, Severity severity, AsyncCallback<List<String>> callback);
    void buildIgTestOrchestration(IgOrchestrationRequest request, AsyncCallback<RawResponse> callback);
    void buildRgTestOrchestration(RgOrchestrationRequest request, AsyncCallback<RawResponse> callback);
	void buildIigTestOrchestration(IigOrchestrationRequest request, AsyncCallback<RawResponse> callback);
	void buildRigTestOrchestration(RigOrchestrationRequest request, AsyncCallback<RawResponse> callback);
	void buildIdsTestOrchestration(IdsOrchestrationRequest request, AsyncCallback<RawResponse> callback);
	void buildRepTestOrchestration(RepOrchestrationRequest request, AsyncCallback<RawResponse> callback);
	void getSiteNamesWithRIG(AsyncCallback<List<String>> callback) throws Exception;
	void getSiteNamesWithIDS(AsyncCallback<List<String>> callback) throws Exception;
	void register(String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params, AsyncCallback<Result> callback) throws Exception;
	void registerWithLocalizedTrackingInODDS(String username, TestInstance testInstance, SiteSpec registry, SimId oddsSimId, Map<String, String> params, AsyncCallback<Map<String, String>> callback);
	void getOnDemandDocumentEntryDetails(SimId oddsSimId, AsyncCallback<List<DocumentEntryDetail>> callback);
	void getInteractionFromModel(InteractingEntity model, AsyncCallback<InteractingEntity> callback);


	void getServletContextName(AsyncCallback<String> callback);
	void retrieveConfiguredFavoritesPid(String environment, AsyncCallback<List<Pid>> callback) throws IOException;

	void getAssignedSiteForTestSession(String testSession, AsyncCallback<String> async);

	void setAssignedSiteForTestSession(String testSession, String siteName, AsyncCallback<Void> async);
}
