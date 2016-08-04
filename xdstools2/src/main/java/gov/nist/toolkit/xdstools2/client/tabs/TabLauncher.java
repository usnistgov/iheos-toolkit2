package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.RegistryObject;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataEditorTab;
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

@Deprecated
public class TabLauncher implements ClickHandler {
	TabContainer container;
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
	final static public String documentRetrieveTabLabel = "RetrieveDocuments";
	final static public String allocatePatientIdTabLabel = "Allocate Patient ID for the Public Registry";
	final static public String registryTestDataTabLabel = "XDS Register";
	final static public String dashboardTabLabel = "Dashboard";
	final static public String repositoryTestDataTabLabel = "XDS Provide & Register";
	final static public String recipientTestDataTabLabel = "XDR Provide & Register";
	final static public String repositoryDoThisFirstTabLabel = "XDS.b_Repository_Do_This_First";
	final static public String registryLifecycleTabLabel = "XDS.b_Lifecycle";
	final static public String registryFolderHandlingTabLabel = "XDS.b_Registry_Folder_Handling";
	final static public String adminTabLabel = "Site/Actor Configuration";
	final static public String repositoryTabLabel = "Repository Listing";
	final static public String mesaTabLabel = "Pre-Connectathon Tests";
	final static public String testRunnerTabLabel = "Conformance Tests";
	final static public String nwhinTabLabel = "Pre-OnBoarding Tests";
	final static public String pidFavoritesLabel = "Manage Patient IDs";
	final static public String testsOverviewTabLabel = "Tests Overview";
    final static public String igTestsTabLabel = "Initiating Gateway Tests";
	final static public String iigTestsTabLabel = "Initiating Imaging Gateway Tests";
    final static public String rgTestsTabLabel = "Responding Gateway Tests";
	final static public String imagingDocumentSetRetrieveTabLabel = "RetrieveImagingDocumentSet";
	final static public String idsTestsTabLabel = "Imaging Document Source Tests";



	final static public String testLogLabel = "Test Log Listing";
	final static public String toolConfigTabLabel = "Toolkit Configuration";	
	final static public String metadataEditorTabLabel = "Action: Edit";
	
	public void onClick(ClickEvent event) {
		if (tabType.equals(findDocumentsTabLabel)) 
			new FindDocumentsTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(igTestsTabLabel))
			new IGTestTab().onAbstractTabLoad(true, "IG Tests");
		else if (tabType.equals(iigTestsTabLabel))
			new IIGTestTab().onAbstractTabLoad(true, "IIG Tests");
		else if (tabType.equals(idsTestsTabLabel))
			new IDSTestTab().onAbstractTabLoad(true, "IDS Tests");
        else if (tabType.equals(rgTestsTabLabel))
            new RGTestTab().onAbstractTabLoad(true, "RG Tests");
        else if (tabType.equals(findDocumentsByRefIdTabLabel))
            new FindDocumentsByRefIdTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(findDocumentsAllParametersTabLabel))
			new FindDocuments2Tab().onAbstractTabLoad(true, null);
		else if (tabType.equals(findPatientTabLabel)) 
			new FindPatientTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(findFoldersTabLabel)) 
			new FindFoldersTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(getDocumentsTabLabel)) 
			new GetDocumentsTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(getFoldersTabLabel)) 
			new GetFoldersTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(getFolderAndContentsTabLabel)) 
			new GetFolderAndContentsTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(mpqFindDocumentsTabLabel)) 
			new MPQFindDocumentsTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(getSubmissionSetTabLabel)) 
			new GetSubmissionSetAndContentsTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(registryDoThisFirstTabLabel)) 
			new RegisterAndQueryTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(getRelatedTabLabel)) 
			new GetRelatedTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(getAllTabLabel))
			new GetAllTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(connectathonTabLabel))
			new ConnectathonTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(srcStoresDocValTabLabel)) 
			new SourceStoredDocValTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(documentRetrieveTabLabel)) 
			new DocRetrieveTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(imagingDocumentSetRetrieveTabLabel)) 
			new ImagingDocSetRetrieveTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(allocatePatientIdTabLabel)) 
			new PidAllocateTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(registryTestDataTabLabel)) 
			new RegistryTestdataTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(repositoryTestDataTabLabel)) 
			new RepositoryTestdataTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(recipientTestDataTabLabel)) 
			new XDRTestdataTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(repositoryDoThisFirstTabLabel)) 
			new ProvideAndRetrieveTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(registryLifecycleTabLabel)) 
			new LifecycleTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(registryFolderHandlingTabLabel)) 
			new FolderTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(adminTabLabel)) 
			new ActorConfigTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(messageValidatorTabLabel)) 
			new MessageValidatorTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(simulatorMessageViewTabLabel)) 
			new SimulatorMessageViewTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(simulatorControlTabLabel)) 
			new SimulatorControlTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(toolConfigTabLabel)) 
			new ToolConfigTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(mesaTabLabel)) 
			new MesaTestTab().onAbstractTabLoad(true, "Pre-Con Tests");
		else if (tabType.equals(nwhinTabLabel))
			new MesaTestTab().onAbstractTabLoad(true, "On-Boarding Tests");
		else if (tabType.equals(testLogLabel)) 
			new ConformanceTestTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(dashboardTabLabel)) 
			new DashboardTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(repositoryTabLabel)) 
			new RepositoryListingTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(pidFavoritesLabel))
			new PidFavoritesTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(testsOverviewTabLabel))
			new TestsOverviewTab().onAbstractTabLoad(true, null);
		else if (tabType.equals(metadataEditorTabLabel)) {
			MetadataEditorTab t = new MetadataEditorTab();
			t.setSiteSpec(siteSpec);
			t.setRegistryObject(ro);
			t.onAbstractTabLoad(true, null);
		}
		
	}
	
	

	public TabLauncher(TabContainer container, String tabType) {
		this.container = container;
		this.tabType = tabType;
	}
	
	public TabLauncher(TabContainer container, String tabType, SiteSpec siteSpec, RegistryObject ro) {
		this.container = container;
		this.tabType = tabType;
		this.siteSpec = siteSpec;
		this.ro = ro;
	}


}
