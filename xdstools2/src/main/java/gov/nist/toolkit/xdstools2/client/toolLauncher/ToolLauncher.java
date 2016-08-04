package gov.nist.toolkit.xdstools2.client.toolLauncher;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.RegistryObject;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.tabs.*;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.IDSTestTab;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.IGTestTab;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.IIGTestTab;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.RGTestTab;
import gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab.ActorConfigTab;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.ConformanceTestTab;
import gov.nist.toolkit.xdstools2.client.tabs.findDocuments2Tab.FindDocuments2Tab;
import gov.nist.toolkit.xdstools2.client.tabs.getAllTab.GetAllTab;
import gov.nist.toolkit.xdstools2.client.tabs.messageValidator.MessageValidatorTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SimulatorControlTab;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.TestsOverviewTab;

import java.util.ArrayList;
import java.util.List;

public class ToolLauncher implements ClickHandler {
	String tabType;
	SiteSpec siteSpec = null;
	RegistryObject ro = null;

	final static public String findDocumentsTabLabel = "FindDocuments";
	final static public String findDocumentsByRefIdTabLabel = "FindDocumentsByRefId";
	final static public String findDocumentsAllParametersTabLabel = "Find Documents (All Parameters)";
	final static public String findPatientTabLabel = "XCPD-FindPatient";
	final static public String findFoldersTabLabel = "FindFolders";
	final static public String getDocumentsTabLabel = "GetDocuments";
	final static public String getFoldersTabLabel = "GetFolders";
	final static public String getFolderAndContentsTabLabel = "GetFolderAndContents";
	final static public String mpqFindDocumentsTabLabel = "MPQ-FindDocuments";
	final static public String getSubmissionSetTabLabel = "GetSubmissionSetAndContents";
	final static public String getAllTabLabel = "GetAll";
	final static public String registryDoThisFirstTabLabel = "XDS.b_Registry_Do_This_First";
	final static public String getRelatedTabLabel = "GetRelated";
	final static public String connectathonTabLabel = "Connectathon Tools";
	final static public String messageValidatorTabLabel = "Message Validator";
	final static public String simulatorMessageViewTabLabel = "Simulator Log Viewer";
	final static public String simulatorControlTabLabel = "Simulator Manager";
	final static public String srcStoresDocValTabLabel = "XDS.b_Doc_Source_Stores_Document";
	final static public String documentRetrieveTabLabel = "RetrieveDocument";
	final static public String allocatePatientIdTabLabel = "Allocate Patient ID for the Public Registry";
	final static public String registryTestDataTabLabel = "XDS Register";
	final static public String dashboardTabLabel = "Dashboard";
	final static public String repositoryTestDataTabLabel = "XDS Provide & Register";
	final static public String recipientTestDataTabLabel = "XDR Provide & Register";
	final static public String repositoryDoThisFirstTabLabel = "XDS.b_Repository_Do_This_First";
	final static public String registryLifecycleTabLabel = "XDS.b_Lifecycle";
	final static public String registryFolderHandlingTabLabel = "XDS.b_Registry_Folder_Handling";
	final static public String sitesTabLabel = "Site/Actor Configuration";
	final static public String repositoryTabLabel = "Repository Listing";
	final static public String mesaTabLabel = "Pre-Connectathon Tests";
	final static public String testRunnerTabLabel = "Conformance Tests";
	final static public String nwhinTabLabel = "Pre-OnBoarding Tests";
	final static public String pidFavoritesLabel = "Manage Patient IDs";
	final static public String testsOverviewTabLabel = "Tests Overview";
	final static public String igTestsTabLabel = "Initiating Gateway Tests";
	final static public String rgTestsTabLabel = "Responding Gateway Tests";
	final static public String iigTestsTabLabel = "Initiating Imaging Gateway Tests";
	final static public String idsTestsTabLabel = "Imaging Document Source Tests";
	final static public String imagingDocumentSetRetrieveTabLabel = "RetrieveImagingDocumentSet";
	final static public String homeTabLabel = "Home";
	final static public String adminTabLabel = "Site/Actor Configuration";



	final static public String testLogLabel = "Test Log Listing";
	final static public String toolConfigTabLabel = "Toolkit Configuration";

	private static List<ToolDef> tools = new ArrayList<>();

	//                         Menu Name                            Tab name    URL name
	static  {
		tools.add(new ToolDef(findDocumentsAllParametersTabLabel, "FindDocs", "FindDocs"));
		tools.add(new ToolDef(findDocumentsTabLabel, "FindDocs", "FindDocs"));
		tools.add(new ToolDef(findDocumentsByRefIdTabLabel, "DocsByRefId", "DocsByRefId"));
//		tools.add(new ToolDef(findPatientTabLabel, "FindPatients", "FindPatients"));
		tools.add(new ToolDef(findFoldersTabLabel, "FindFolders", "FindFolders"));
		tools.add(new ToolDef(getDocumentsTabLabel, "GetDocs", "GetDocs"));
		tools.add(new ToolDef(getFoldersTabLabel, "GetFolders", "GetFolders"));
		tools.add(new ToolDef(getFolderAndContentsTabLabel, "GetFolderContents", "GetFolderContents"));
		tools.add(new ToolDef(mpqFindDocumentsTabLabel, "MPQ", "MPQ"));
		tools.add(new ToolDef(getSubmissionSetTabLabel, "GetSS", "GetSS"));
		tools.add(new ToolDef(getAllTabLabel, "GetAll", "GetAll"));
		tools.add(new ToolDef(registryDoThisFirstTabLabel, "RegFirst", "RegFirst"));
		tools.add(new ToolDef(getRelatedTabLabel, "GetRelated", "GetRelated"));
		tools.add(new ToolDef(connectathonTabLabel, "CAT", "CAT"));
		tools.add(new ToolDef(messageValidatorTabLabel, "MsgVal", "MsgVal"));
		tools.add(new ToolDef(simulatorMessageViewTabLabel, "SimMsgs", "SimMsgs"));
		tools.add(new ToolDef(simulatorControlTabLabel, "SimCntl", "SimCntl"));
		tools.add(new ToolDef(srcStoresDocValTabLabel, "SrcStores", "SrcStores"));
		tools.add(new ToolDef(documentRetrieveTabLabel, "DocRet", "DocRet"));
//		tools.add(new ToolDef(allocatePatientIdTabLabel, "FindDocs", "FindDocs"));
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
//		tools.add(new ToolDef(testRunnerTabLabel, "FindDocs", "FindDocs"));
//		tools.add(new ToolDef(nwhinTabLabel, "FindDocs", "FindDocs"));
		tools.add(new ToolDef(pidFavoritesLabel, "PIDFav", "PIDFav"));
		tools.add(new ToolDef(testsOverviewTabLabel, "TestsOver", "TestsOver"));
		tools.add(new ToolDef(igTestsTabLabel, "IGTests", "IGTests"));
		tools.add(new ToolDef(iigTestsTabLabel, "IIGTests", "IIGTests"));
		tools.add(new ToolDef(idsTestsTabLabel, "IDSTests", "IDSTests"));
		tools.add(new ToolDef(rgTestsTabLabel, "RGTests", "RGTests"));
		tools.add(new ToolDef(imagingDocumentSetRetrieveTabLabel, "RetIDS", "RetIDS"));
		tools.add(new ToolDef(testLogLabel, "TestLog", "TestLog"));
		tools.add(new ToolDef(toolConfigTabLabel, "ToolkitConf", "ToolkitConf"));
		tools.add(new ToolDef(homeTabLabel, "Home", "Home"));
		tools.add(new ToolDef(adminTabLabel, "Admin", "Admin"));
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

		if (menuName.equals(mpqFindDocumentsTabLabel))
			return new MPQFindDocumentsTab();

		if (menuName.equals(findDocumentsAllParametersTabLabel)) return new FindDocuments2Tab();
		if (menuName.equals(findDocumentsTabLabel)) return new FindDocumentsTab();
		if (menuName.equals(igTestsTabLabel)) return new IGTestTab();
		if (menuName.equals(rgTestsTabLabel)) return new RGTestTab();
		if (menuName.equals(findDocumentsByRefIdTabLabel)) return new FindDocumentsByRefIdTab();
		if (menuName.equals(findDocumentsAllParametersTabLabel)) return new FindDocuments2Tab();
		if (menuName.equals(findPatientTabLabel)) return new FindPatientTab();
		if (menuName.equals(findFoldersTabLabel)) return new FindFoldersTab();
		if (menuName.equals(getDocumentsTabLabel)) return new GetDocumentsTab();
		if (menuName.equals(getFoldersTabLabel)) return new GetFoldersTab();
		if (menuName.equals(getFolderAndContentsTabLabel)) return new GetFolderAndContentsTab();
		if (menuName.equals(getSubmissionSetTabLabel)) return new GetSubmissionSetAndContentsTab();
		if (menuName.equals(registryDoThisFirstTabLabel)) return new RegisterAndQueryTab();
		if (menuName.equals(getRelatedTabLabel)) return new GetRelatedTab();
		if (menuName.equals(getAllTabLabel)) return new GetAllTab();
		if (menuName.equals(connectathonTabLabel)) return new ConnectathonTab();
		if (menuName.equals(srcStoresDocValTabLabel)) return new SourceStoredDocValTab();
		if (menuName.equals(documentRetrieveTabLabel)) return new DocRetrieveTab();
		if (menuName.equals(imagingDocumentSetRetrieveTabLabel)) return new ImagingDocSetRetrieveTab();
		if (menuName.equals(registryTestDataTabLabel)) return new RegistryTestdataTab();
		if (menuName.equals(repositoryTestDataTabLabel)) return new RepositoryTestdataTab();
		if (menuName.equals(recipientTestDataTabLabel)) return new XDRTestdataTab();
		if (menuName.equals(repositoryDoThisFirstTabLabel)) return new ProvideAndRetrieveTab();
		if (menuName.equals(registryLifecycleTabLabel)) return new LifecycleTab();
		if (menuName.equals(registryFolderHandlingTabLabel)) return new FolderTab();
		if (menuName.equals(sitesTabLabel)) return new ActorConfigTab();
		if (menuName.equals(messageValidatorTabLabel)) return new MessageValidatorTab();
		if (menuName.equals(simulatorMessageViewTabLabel)) return new SimulatorMessageViewTab();
		if (menuName.equals(simulatorControlTabLabel)) return new SimulatorControlTab();
		if (menuName.equals(toolConfigTabLabel)) return new ToolConfigTab();
		if (menuName.equals(mesaTabLabel)) return new MesaTestTab();
		if (menuName.equals(nwhinTabLabel)) return new MesaTestTab();
		if (menuName.equals(testLogLabel)) return new ConformanceTestTab();
		if (menuName.equals(dashboardTabLabel)) return new DashboardTab();
		if (menuName.equals(repositoryTabLabel)) return new RepositoryListingTab();
		if (menuName.equals(pidFavoritesLabel)) return new PidFavoritesTab();
		if (menuName.equals(testsOverviewTabLabel)) return new TestsOverviewTab();
		if (menuName.equals(homeTabLabel)) return new HomeTab();
		if (menuName.equals(iigTestsTabLabel)) return new IIGTestTab();
		if (menuName.equals(idsTestsTabLabel)) return new IDSTestTab();
		return null;
	}

	private void launch(String requestedName) {
		ToolDef def = getToolDef(requestedName);
		ToolWindow tool = getTool(def);
		if (tool == null) return;
		tool.onAbstractTabLoad(true, def.tabName);
	}

	public void launch() {
		launch(tabType);
	}

	public void onClick(ClickEvent event) {
		launch(tabType);
	}

	public ToolLauncher(String tabType) {
		this.tabType = tabType;
	}

	public ToolLauncher(String tabType, SiteSpec siteSpec, RegistryObject ro) {
		this.tabType = tabType;
		this.siteSpec = siteSpec;
		this.ro = ro;
	}


}
