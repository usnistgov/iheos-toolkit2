package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actorfactory.client.*;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.services.client.IgOrchestationManagerRequest;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ToolkitServiceAsync {

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
	void getEnvironmentNames(AsyncCallback<List<String>> callback);
	void isGazelleConfigFeedEnabled(AsyncCallback<Boolean> callback);
	void reloadSystemFromGazelle(String systemName, AsyncCallback<String> callback);
	void getSiteNamesWithRG(AsyncCallback<List<String>> callback);

	void getDashboardRegistryData(AsyncCallback<List<RegistryStatus>> callback);
	void getDashboardRepositoryData(AsyncCallback<List<RepositoryStatus>> callback);

	void getLogContent(String sessionName, TestInstance testInstance, AsyncCallback<List<Result>> callback);
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

	void getTransactionOfferings(AsyncCallback<TransactionOfferings> callback) throws Exception;
	void getRegistryNames(AsyncCallback<List<String>> callback);
	void getRepositoryNames(AsyncCallback<List<String>> callback);
	void getRGNames(AsyncCallback<List<String>> callback);
	void getIGNames(AsyncCallback<List<String>> callback);
	void getRawLogs(TestInstance logId, AsyncCallback<TestLogs> callback);
	void getTestdataSetListing(String testdataSetName, AsyncCallback<List<String>> callback);
	void getCodesConfiguration(AsyncCallback<CodesResult> callback);
	void getSite(String siteName, AsyncCallback<Site> callback);
	void getAllSites(AsyncCallback<Collection<Site>> callback);
	void saveSite(Site site, AsyncCallback<String> callback);
	void reloadSites(boolean simAlso, AsyncCallback<List<String>> callback);
	void reloadExternalSites(AsyncCallback<List<String>> callback);
	void deleteSite(String siteName, AsyncCallback<String> callback);

	void getSSandContents(SiteSpec site, String ssuid, AsyncCallback<List<Result>> callback);
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
	void submitRegistryTestdata(SiteSpec site, String datasetName, String pid, AsyncCallback<List<Result>> callback);	
	void submitRepositoryTestdata(SiteSpec site, String datasetName, String pid, AsyncCallback<List<Result>> callback);	
	void submitXDRTestdata(SiteSpec site, String datasetName, String pid, AsyncCallback<List<Result>> callback);	
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
	void getAllSimConfigs(String user, AsyncCallback<List<SimulatorConfig>> callback) throws Exception;
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
	void getCollection(String collectionSetName, String collectionName, AsyncCallback<Map<String, String>> callback);
	void getTestReadme(String test, AsyncCallback<String> callback);
	void getTestIndex(String test, AsyncCallback<List<String>> callback);
	void runMesaTest(String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure, AsyncCallback<List<Result>> callback);
	void isPrivateMesaTesting(AsyncCallback<Boolean> callback);
	void addMesaTestSession(String name, AsyncCallback<Boolean> callback);
	void delMesaTestSession(String name, AsyncCallback<Boolean> callback);
	void createPid(String assigningAuthority, AsyncCallback<Pid> callback) throws NoServletSessionException;
	void getAssigningAuthority(AsyncCallback<String> callback) throws Exception;
	void getAssigningAuthorities(AsyncCallback<List<String>> callback) throws Exception;
	void sendPidToRegistry(SiteSpec site, Pid pid, AsyncCallback<List<Result>> callback) throws NoServletSessionException;
	void getSimulatorEventRequest(TransactionInstance ti, AsyncCallback<Result> callback) throws Exception;
	void getSimulatorEventResponse(TransactionInstance ti, AsyncCallback<Result> callback) throws Exception;


	void getTestplanAsText(TestInstance testInstance, String section, AsyncCallback<String> callback);
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
	void getMesaTestSessionNames(AsyncCallback<List<String>> callback);
	void deleteAllTestResults(Site site, AsyncCallback<List<Test>> callback);
	void deleteSingleTestResult(Site site, int testId, AsyncCallback<Test> callback);
	void runAllTests(Site site, AsyncCallback<List<Test>> callback);
	void runSingleTest(Site site, int testId, AsyncCallback<Test> callback);
    void getProfileErrorCodeRefs(String transactionName, Severity severity, AsyncCallback<List<String>> callback);
    void buildIgTestOrchestration(IgOrchestationManagerRequest request, AsyncCallback<RawResponse> callback);

}
