package gov.nist.toolkit.xdstools2.client.toolLauncher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import gov.nist.toolkit.actortransaction.shared.IheItiProfile;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.installation.shared.ToolkitUserMode;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.tabs.*;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.SubmitResource;
import gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab.ActorConfigTab;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.ConformanceTestTab;
import gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab.FhirSearch;
import gov.nist.toolkit.xdstools2.client.tabs.findDocuments2Tab.FindDocuments2Tab;
import gov.nist.toolkit.xdstools2.client.tabs.getAllTab.GetAllTab;
import gov.nist.toolkit.xdstools2.client.tabs.messageValidator.MessageValidatorTab;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewer;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SimConfigEditorTabLoader;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SimulatorControlTab;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.TestsOverviewTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameter;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameterMap;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolLauncher implements ClickHandler {
	private ToolParameterMap tpm;
	private String tabType;

	final static public String findDocumentsTabLabel = "FindDocuments";
	final static public String findDocumentsByRefIdTabLabel = "FindDocumentsByRefId";
	final static public String findDocumentsAllParametersTabLabel = "Find Documents (All Parameters)";
	final static public String findFoldersTabLabel = "FindFolders";
	final static public String getDocumentsTabLabel = "GetDocuments";
	final static public String getFoldersTabLabel = "GetFolders";
	final static public String getFolderAndContentsTabLabel = "GetFolderAndContents";
	final static public String mpqFindDocumentsTabLabel = "MPQ-FindDocuments";
	final static public String getSubmissionSetTabLabel = "GetSubmissionSetAndContents";
	final static public String getAllTabLabel = "GetAll";
	final static public String fhirSearchTabLabel = "FHIR Search";
	final static public String registryDoThisFirstTabLabel = "XDS.b_Registry_Do_This_First";
	final static public String rmdInitTabLabel = "RMD Test Initialization";
	final static public String getRelatedTabLabel = "GetRelated";
	final static public String connectathonTabLabel = "Connectathon Tools";
	final static public String messageValidatorTabLabel = "Message Validator";
	final static public String simulatorMessageViewTabLabel = "Simulator Logs";
	final static public String newSimulatorMessageViewTabLabel = "New Simulator Logs";
	final static public String simulatorControlTabLabel = "Simulators";
	final static public String simulatorConfigEditTabLabel = "SimConfig";
	final static public String srcStoresDocValTabLabel = "XDS.b_Doc_Source_Stores_Document";
	final static public String documentRetrieveTabLabel = "RetrieveDocuments";
	final static public String allocatePatientIdTabLabel = "Allocate Patient ID for the Public Registry";
	final static public String registryTestDataTabLabel = "XDS Register";
	final static public String dashboardTabLabel = "Dashboard";
	final static public String repositoryTestDataTabLabel = "XDS Provide & Register";
	final static public String recipientTestDataTabLabel = "XDR Provide & Register";
	final static public String repositoryDoThisFirstTabLabel = "XDS.b_Repository_Do_This_First";
	final static public String registryLifecycleTabLabel = "XDS.b_Lifecycle";
	final static public String registryFolderHandlingTabLabel = "XDS.b_Registry_Folder_Handling";
	final static public String sitesTabLabel = "System Configurations";
	final static public String repositoryTabLabel = "Repository Listing";
	final static public String mesaTabLabel = "Pre-Connectathon Tests";
	final static public String testRunnerTabLabel = "Conformance Tests";
	final static public String pidFavoritesLabel = "Manage Patient IDs";
	final static public String testsOverviewTabLabel = "Tests Overview";
//	final static public String igTestsTabLabel = "Initiating Gateway Tests";
//	final static public String rgTestsTabLabel = "Responding Gateway Tests";
//	final static public String iigTestsTabLabel = "Initiating Imaging Gateway Tests";
//	final static public String rigTestsTabLabel = "Responding Imaging Gateway Tests";
//	final static public String idsTestsTabLabel = "Imaging Document Source Tests";
//	final static public String rsnaedgeTestsTabLabel = "RSNA Edge Device Tests";
	final static public String imagingDocumentSetRetrieveTabLabel = "RetrieveImagingDocumentSet";
	final static public String homeTabLabel = "Home";
	final static public String SysConfigTabLabel = "SUT Configuration";
	final static public String submitResourceTabLabel = "Submit Resource";

	final static public String conformanceTestsLabel = "Conformance Tests";
	final static public String toolConfigTabLabel = "Toolkit configuration";

	private ToolUserProperties toolUserProperties;
	private class ToolUserProperties {
		private final boolean multiUserModeEnabled;
		private final boolean casModeEnabled;
		private final ToolkitUserMode userMode;
		private final String currentEnvironment;
		private final String requestedTestSession;
		private final String requestedEnvironment;

		public ToolUserProperties(ToolParameterMap tpm, Map<String,String> tkPropMap) {
			multiUserModeEnabled = Boolean.parseBoolean(tkPropMap.get("Multiuser_mode"));
			casModeEnabled = Boolean.parseBoolean(tkPropMap.get("Cas_mode"));
			userMode = (multiUserModeEnabled) ? (casModeEnabled ? ToolkitUserMode.CAS_USER : ToolkitUserMode.MULTI_USER) : ToolkitUserMode.SINGLE_USER;
			currentEnvironment = tkPropMap.get("Default_Environment");
			if (tpm!=null) {
				requestedEnvironment = tpm.getValue(ToolParameter.ENVIRONMENT);
				requestedTestSession = tpm.getValue(ToolParameter.TEST_SESSION);
			} else {
				requestedEnvironment = null;
				requestedTestSession = null;
			}
		}
	}

	private static List<ToolDef> tools = new ArrayList<>();

	//                         Menu Name                            Tab name    URL name
	static  {
		tools.add(new ToolDef(findDocumentsAllParametersTabLabel, "FindDocs", "FindDocs"));
		tools.add(new ToolDef(findDocumentsTabLabel, "FindDocs", "FindDocs"));
		tools.add(new ToolDef(findDocumentsByRefIdTabLabel, "DocsByRefId", "DocsByRefId"));
//		tools.addTest(new ToolDef(findPatientTabLabel, "FindPatients", "FindPatients"));
		tools.add(new ToolDef(findFoldersTabLabel, "FindFolders", "FindFolders"));
		tools.add(new ToolDef(getDocumentsTabLabel, "GetDocs", "GetDocs"));
		tools.add(new ToolDef(getFoldersTabLabel, "GetFolders", "GetFolders"));
		tools.add(new ToolDef(getFolderAndContentsTabLabel, "GetFolderContents", "GetFolderContents"));
		tools.add(new ToolDef(mpqFindDocumentsTabLabel, "MPQ", "MPQ"));
		tools.add(new ToolDef(getSubmissionSetTabLabel, "GetSS", "GetSS"));
		tools.add(new ToolDef(getAllTabLabel, "GetAll", "GetAll"));
		tools.add(new ToolDef(registryDoThisFirstTabLabel, "RegFirst", "RegFirst"));
		tools.add(new ToolDef(rmdInitTabLabel, "RMDInit", "RMDInit"));
		tools.add(new ToolDef(getRelatedTabLabel, "GetRelated", "GetRelated"));
		tools.add(new ToolDef(connectathonTabLabel, "Connectathon", "Connectathon"));
		tools.add(new ToolDef(messageValidatorTabLabel, "MsgVal", "MsgVal"));
		tools.add(new ToolDef(simulatorMessageViewTabLabel, "SimMsgs", "SimMsgs"));
		tools.add(new ToolDef(newSimulatorMessageViewTabLabel, "New SimMsgs", "New SimMsgs"));
		tools.add(new ToolDef(simulatorControlTabLabel, "SimCntl", "SimCntl"));
		tools.add(new ToolDef(simulatorConfigEditTabLabel, "SimConfig", "SimConfig"));
		tools.add(new ToolDef(srcStoresDocValTabLabel, "SrcStores", "SrcStores"));
		tools.add(new ToolDef(documentRetrieveTabLabel, "DocRet", "DocRet"));
//		tools.addTest(new ToolDef(allocatePatientIdTabLabel, "FindDocs", "FindDocs"));
		tools.add(new ToolDef(registryTestDataTabLabel, "RegData", "RegData"));
		tools.add(new ToolDef(dashboardTabLabel, "Dash", "Dash"));
		tools.add(new ToolDef(repositoryTestDataTabLabel, "RepData", "RepData"));
		tools.add(new ToolDef(recipientTestDataTabLabel, "RecData", "RecData"));
		tools.add(new ToolDef(repositoryDoThisFirstTabLabel, "RepFirst", "RepFirst"));
		tools.add(new ToolDef(registryLifecycleTabLabel, "RegLifeCycle", "RegLifeCycle"));
		tools.add(new ToolDef(registryFolderHandlingTabLabel, "Folder", "Folder"));
		tools.add(new ToolDef(sitesTabLabel, "Sites", "Sites"));
		tools.add(new ToolDef(repositoryTabLabel, "Repositories", "Repositories"));
		tools.add(new ToolDef(mesaTabLabel, "PreCAT", "PreCAT"));
//		tools.addTest(new ToolDef(testRunnerTabLabel, "FindDocs", "FindDocs"));
//		tools.addTest(new ToolDef(nwhinTabLabel, "FindDocs", "FindDocs"));
		tools.add(new ToolDef(pidFavoritesLabel, "PIDFav", "PIDFav"));
		tools.add(new ToolDef(testsOverviewTabLabel, "TestsOver", "TestsOver"));
//		tools.add(new ToolDef(igTestsTabLabel, "IGTests", "IGTests"));
//		tools.add(new ToolDef(iigTestsTabLabel, "IIGTests", "IIGTests"));
//		tools.add(new ToolDef(rigTestsTabLabel, "RIGTests", "RIGTests"));
//		tools.add(new ToolDef(idsTestsTabLabel, "IDSTests", "IDSTests"));
//		tools.add(new ToolDef(rsnaedgeTestsTabLabel, "RSNA Edge Tests", "RSNAEdgeTests"));
//		tools.add(new ToolDef(rgTestsTabLabel, "RGTests", "RGTests"));
		tools.add(new ToolDef(imagingDocumentSetRetrieveTabLabel, "RetIDS", "RetIDS"));
		tools.add(new ToolDef(conformanceTestsLabel, "ConfTests", "ConfTests"));
		tools.add(new ToolDef(toolConfigTabLabel, "ToolkitConf", "ToolkitConf"));
		tools.add(new ToolDef(homeTabLabel, "Home", "Home"));
		tools.add(new ToolDef(SysConfigTabLabel, "Admin", "Admin"));
		tools.add(new ToolDef(submitResourceTabLabel, "Submit Resource", "SubmitResource"));
		tools.add(new ToolDef(fhirSearchTabLabel, "FhirSearch", "FhirSearch"));
	}

	private ToolDef getToolDef(String requestedName) {
		for (ToolDef def : tools) {
			if (def.isNamed(requestedName)) {
				return def;
			}
		}
		return null;
	}

	private ToolWindow getTool(ToolDef def) {
		if (def == null) return null;
		String menuName = def.getMenuName();

		if (menuName.equals(mpqFindDocumentsTabLabel)) {
			return new MPQFindDocumentsTab();
		}

		if (menuName.equals(findDocumentsAllParametersTabLabel)) {
			return new FindDocuments2Tab();
		}
		if (menuName.equals(findDocumentsTabLabel)) {
			return new FindDocumentsTab();
		}
//		if (menuName.equals(igTestsTabLabel)) return new IGTestTab();
//		if (menuName.equals(rgTestsTabLabel)) return new RGTestTab();
		if (menuName.equals(findDocumentsByRefIdTabLabel)) {
			return new FindDocumentsByRefIdTab();
		}
		if (menuName.equals(findDocumentsAllParametersTabLabel)) {
			return new FindDocuments2Tab();
		}
		if (menuName.equals(findFoldersTabLabel)) {
			return new FindFoldersTab();
		}
		if (menuName.equals(getDocumentsTabLabel)) {
			return new GetDocumentsTab();
		}
		if (menuName.equals(getFoldersTabLabel)) {
			return new GetFoldersTab();
		}
		if (menuName.equals(getFolderAndContentsTabLabel)) {
			return new GetFolderAndContentsTab();
		}
		if (menuName.equals(getSubmissionSetTabLabel)) {
			return new GetSubmissionSetAndContentsTab();
		}
		if (menuName.equals(registryDoThisFirstTabLabel)) {
			return new RegisterAndQueryTab();
		}
		if (menuName.equals(rmdInitTabLabel)) {
			return new RMDInitTab();
		}
		if (menuName.equals(getRelatedTabLabel)) {
			return new GetRelatedTab();
		}
		if (menuName.equals(getAllTabLabel)) {
			return new GetAllTab();
		}
		if (menuName.equals(connectathonTabLabel)) {
			return new ConnectathonTab();
		}
		if (menuName.equals(srcStoresDocValTabLabel)) {
			return new SourceStoredDocValTab();
		}
		if (menuName.equals(documentRetrieveTabLabel)) {
			return new DocRetrieveTab();
		}
		if (menuName.equals(imagingDocumentSetRetrieveTabLabel)) {
			return new ImagingDocSetRetrieveTab();
		}
		if (menuName.equals(registryTestDataTabLabel)) {
			return new RegistryTestdataTab();
		}
		if (menuName.equals(repositoryTestDataTabLabel)) {
			return new RepositoryTestdataTab();
		}
		if (menuName.equals(recipientTestDataTabLabel)) {
			return new XDRTestdataTab();
		}
		if (menuName.equals(repositoryDoThisFirstTabLabel)) {
			return new ProvideAndRetrieveTab();
		}
		if (menuName.equals(registryLifecycleTabLabel)) {
			return new LifecycleTab();
		}
		if (menuName.equals(registryFolderHandlingTabLabel)) {
			return new FolderTab();
		}
		if (menuName.equals(sitesTabLabel)) {
			return new ActorConfigTab();
		}
		if (menuName.equals(messageValidatorTabLabel)) {
			return new MessageValidatorTab();
		}
		if (menuName.equals(simulatorMessageViewTabLabel)) {
			return new SimulatorMessageViewTab();
		}
		if (menuName.equals(newSimulatorMessageViewTabLabel)) {
			return new NewToolLauncher().launch(new SimMsgViewer());
		}
		if (menuName.equals(simulatorControlTabLabel)) {
			return new SimulatorControlTab();
		}
		if (menuName.equals(simulatorConfigEditTabLabel)) {
		    if (tpm!=null) {
				// Environment
                String requestedEnvironment = tpm.getValue(ToolParameter.ENVIRONMENT);
				if (requestedEnvironment == null || "".equals(requestedEnvironment)) {
					throw new ToolkitRuntimeException("env parameter is required.");
				}
				// Test session
				String requestedTestSession = tpm.getValue(ToolParameter.TEST_SESSION);
				if (requestedTestSession == null || "".equals(requestedTestSession)) {
					throw new ToolkitRuntimeException("testSession parameter is required.");
				}
				// systemId
				String systemId = tpm.getValue(ToolParameter.SYSTEM_ID);
				if (systemId == null || "".equals(systemId)) {
					throw new ToolkitRuntimeException("systemId parameter is required.");

				}
				SimConfigEditorTabLoader tabLoader = new SimConfigEditorTabLoader();
				tabLoader.load(tpm);
				return tabLoader.getTab();
			}
		}
		if (menuName.equals(toolConfigTabLabel)) {
			return new ToolConfigTab();
		}
		if (menuName.equals(mesaTabLabel)) {
			return new MesaTestTab();
		}
		if (menuName.equals(conformanceTestsLabel)) {
			ConformanceTestTab conformanceTestTab = new ConformanceTestTab();

			if (tpm == null) {
				// No state, so just present bare tool with an overview to manually select actor/profile/option
				return conformanceTestTab;
			}


			setupEnvironment(toolUserProperties.userMode, toolUserProperties.requestedEnvironment, toolUserProperties.currentEnvironment);
			setupTestSession(toolUserProperties.userMode, toolUserProperties.requestedTestSession, conformanceTestTab);

			conformanceTestTab.getCurrentActorOption().setActorTypeId(tpm.getValue(ToolParameter.ACTOR));
			conformanceTestTab.getCurrentActorOption().setProfileId(IheItiProfile.find(tpm.getValue(ToolParameter.PROFILE)));
			conformanceTestTab.getCurrentActorOption().setOptionId(tpm.getValue(ToolParameter.OPTION));
			SiteSpec site = new SiteSpec(tpm.getValue(ToolParameter.SYSTEM_ID), new TestSession(toolUserProperties.requestedTestSession));
			conformanceTestTab.setCommonSiteSpec(site);
			conformanceTestTab.setSiteToIssueTestAgainst(site);

			GWT.log("Launch ConformanceTool for " +
					"/testsession=" + toolUserProperties.requestedTestSession +
					"/actor=" + conformanceTestTab.getCurrentActorOption().getActorTypeId() +
					"/profile=" + conformanceTestTab.getCurrentActorOption().getProfileId() +
					"/option=" + conformanceTestTab.getCurrentActorOption().getOptionId() +
					"/site=" + site
			);

			return conformanceTestTab;
		}
		if (menuName.equals(dashboardTabLabel)) {
			return new DashboardTab();
		}
		if (menuName.equals(repositoryTabLabel)) {
			return new RepositoryListingTab();
		}
		if (menuName.equals(pidFavoritesLabel)) {
			return new PidFavoritesTab(def.getTabName());
		}
		if (menuName.equals(testsOverviewTabLabel)) {
			return new TestsOverviewTab();
		}
		if (menuName.equals(homeTabLabel)) {
			return new HomeTab();
		}
//		if (menuName.equals(iigTestsTabLabel)) return new IIGTestTab();
//		if (menuName.equals(rigTestsTabLabel)) return new RIGTestTab();
//		if (menuName.equals(idsTestsTabLabel)) return new IDSTestTab();
//		if (menuName.equals(rsnaedgeTestsTabLabel)) return new RSNAEdgeTestTab();
		if (menuName.equals(submitResourceTabLabel)) {
			return new NewToolLauncher().launch(new SubmitResource());
		}
		if (menuName.equals(fhirSearchTabLabel)) {
			return new NewToolLauncher().launch(new FhirSearch());
		}
		return null;
	}

	/**
	 *  NOTE: CAS user mode will be automatically considered for setupEnvironment, and will be silently ignored if it does not apply.
	 * @param userMode
	 * @param requestedEnvironment
	 * @param currentEnvironment
	 */
	private void setupEnvironment(ToolkitUserMode userMode, String requestedEnvironment, String currentEnvironment) {
		if (!ToolkitUserMode.CAS_USER.equals(userMode)
				&& requestedEnvironment!=null && !"".equals(requestedEnvironment)
				&& !requestedEnvironment.equalsIgnoreCase(currentEnvironment)) {
			// Override start-up initialization of environment
			ClientUtils.INSTANCE.getEnvironmentState().initEnvironmentName(requestedEnvironment);
			if (ClientUtils.INSTANCE.getEnvironmentState().isFirstManager()) {
				ClientUtils.INSTANCE.getEnvironmentManager().change(requestedEnvironment);
			}
		}
	}
	private void setupTestSession(ToolkitUserMode userMode, String testSession, ToolWindow toolWindow) {
		if ("default".equalsIgnoreCase(testSession)) {
			if (ToolkitUserMode.SINGLE_USER.equals(userMode)) {
				ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(testSession); // This is needed so that Sign-In selector doesn't overwrite the requested test session with default-test-session
                if (toolWindow!=null) {
					toolWindow.setCurrentTestSession(testSession);
				}
				ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, testSession, "ToolLauncher"));
			} else {
				new PopupMessage("Test session "+ testSession +" cannot be selected in " + userMode +".");
			}
		} else {
			ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(testSession);
			if (toolWindow!=null) {
				toolWindow.setCurrentTestSession(testSession);
			}
			ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, testSession, "ToolLauncher"));
		}
	}

	private ToolWindow launch(String requestedName) {
		toolUserProperties = new ToolUserProperties(tpm, ClientUtils.INSTANCE.getTkPropMap());


		ToolDef def = getToolDef(requestedName);
		final ToolWindow tool = getTool(def);
		if (tool == null) return null;


		if (!tool.loaded()) {
			new PopupMessage(tool.notLoadedReason());
			return null;
		}
		tool.onTabLoad(true, def.tabName);

		if (!requestedName.equals(conformanceTestsLabel)) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					if (tpm != null) {
						setupEnvironment(toolUserProperties.userMode, toolUserProperties.requestedEnvironment, toolUserProperties.currentEnvironment);
						setupTestSession(toolUserProperties.userMode, toolUserProperties.requestedTestSession, tool);
					}
				}
			};
			// Add this to the end of the async queue
			timer.schedule(1);
		}

		return tool;
	}

	public ToolWindow launch() {
		return launch(tabType);
	}

	public void onClick(ClickEvent event) {
		launch(tabType);
	}

	public ToolLauncher(String tabType) {
		this.tabType = tabType;
	}

	public void setTpm(ToolParameterMap tpm) {
		this.tpm = tpm;
	}
}
