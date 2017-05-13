package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.command.command.GetAdminPasswordCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetImplementationVersionCommand;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

import javax.inject.Inject;

public class HomeTab extends GenericQueryTab {
	private String aboutMessage = null;
	private HorizontalFlowPanel menubar = new HorizontalFlowPanel();
	
	@Inject
	XdsTools2AppView xdsTools2AppView;

	public HomeTab() {
		super(new FindDocumentsSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel panel = new FlowPanel();
		panel.add(menubar);

//		menubar.addTest(
//				HyperlinkFactory.launchTool("&nbsp;&nbsp;[" + ToolLauncher.toolConfigTabLabel + "]&nbsp;&nbsp;", new ToolLauncher(ToolLauncher.toolConfigTabLabel))
//		);

		Frame frame = new Frame("site/index.html");
		frame.setSize("100em", "100em");
		panel.add(frame);
		return panel;
	}

	@Override
	protected void bindUI() {
		String th = "";

		try {
			th = xdsTools2AppView.tkProps().get("toolkit.home","");
		} catch (Throwable t) {

		}

		mainGrid = new FlexTable();
		mainGrid.setCellSpacing(20);

		loadIHEGrid(0);
		new GetAdminPasswordCommand(){
			@Override
			public void onComplete(String result) {
				PasswordManagement.adminPassword = result;
			}
		}.run(getCommandContext());
		loadVersion();
	}

	@Override
	protected void configureTabView() {
		addActorReloader();
	}

//	@Override
////	public void onTabLoad(final xdsTools2AppView container, boolean select, String eventName) {
//	public void onTabLoad(boolean select, String eventName) {
//
//
//
//		select = true;
//		registerTab(select, eventName);
////		tabTopPanel.addTest(new HTML("Menu Bar"));
////		tabTopPanel.
//
//
//	}

	boolean forDirect = false;
	boolean forIHE = false;
	boolean forNwHIN = false;

	class MainGridLoader {

		//@Override
		public void featuresLoadedCallback() {

//				tabTopPanel.addTest(mainGrid);


		}

	}

	public void loadIHEGrid(int startingColumn) {


		xdsTools2AppView.clearMainMenu();

		xdsTools2AppView.addtoMainMenu(addHTML("<h2>Toolkit</h2>"));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.homeTabLabel, new ToolLauncher(ToolLauncher.homeTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.toolConfigTabLabel, new ToolLauncher(ToolLauncher.toolConfigTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.sitesTabLabel, new ToolLauncher(ToolLauncher.sitesTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.pidFavoritesLabel, new ToolLauncher(ToolLauncher.pidFavoritesLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorControlTabLabel, new ToolLauncher(ToolLauncher.simulatorControlTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorMessageViewTabLabel, new ToolLauncher(ToolLauncher.simulatorMessageViewTabLabel)));

		// **********************************************************************

		xdsTools2AppView.addtoMainMenu(addHTML("<h3>Queries & Retrieves</h3>"));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsTabLabel, new ToolLauncher(ToolLauncher.findDocumentsTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsAllParametersTabLabel, new ToolLauncher(ToolLauncher.findDocumentsAllParametersTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsByRefIdTabLabel, new ToolLauncher(ToolLauncher.findDocumentsByRefIdTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mpqFindDocumentsTabLabel, new ToolLauncher(ToolLauncher.mpqFindDocumentsTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getDocumentsTabLabel, new ToolLauncher(ToolLauncher.getDocumentsTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getRelatedTabLabel, new ToolLauncher(ToolLauncher.getRelatedTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findFoldersTabLabel, new ToolLauncher(ToolLauncher.findFoldersTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFoldersTabLabel, new ToolLauncher(ToolLauncher.getFoldersTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFolderAndContentsTabLabel, new ToolLauncher(ToolLauncher.getFolderAndContentsTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getSubmissionSetTabLabel, new ToolLauncher(ToolLauncher.getSubmissionSetTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getAllTabLabel, new ToolLauncher(ToolLauncher.getAllTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.documentRetrieveTabLabel, new ToolLauncher(ToolLauncher.documentRetrieveTabLabel)));

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(ToolLauncher.imagingDocumentSetRetrieveTabLabel)));
		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(ToolLauncher.imagingDocumentSetRetrieveTabLabel)));

		// ***************************************************************************
		// Test data


		xdsTools2AppView.addtoMainMenu(addHTML("<h3>Submit</h3>"));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.registryTestDataTabLabel, new ToolLauncher(ToolLauncher.registryTestDataTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.repositoryTestDataTabLabel, new ToolLauncher(ToolLauncher.repositoryTestDataTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.recipientTestDataTabLabel, new ToolLauncher(ToolLauncher.recipientTestDataTabLabel)));


		// ***************************************************************************
		// Tools

		xdsTools2AppView.addtoMainMenu(addHTML("<h3>Other Tools</h3>"));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.repositoryTabLabel, new ToolLauncher(ToolLauncher.repositoryTabLabel)));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.connectathonTabLabel, new ToolLauncher(ToolLauncher.connectathonTabLabel)));

//		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.messageValidatorTabLabel, new ToolLauncher(ToolLauncher.messageValidatorTabLabel)));

		// ***************************************************************************
		// Tests

		xdsTools2AppView.addtoMainMenu(addHTML("<h3>Testing</h3>"));

		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.conformanceTestsLabel, new ToolLauncher(ToolLauncher.conformanceTestsLabel)));

//		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mesaTabLabel, new ToolLauncher(ToolLauncher.mesaTabLabel)));
//
//		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.igTestsTabLabel, new ToolLauncher(ToolLauncher.igTestsTabLabel)));
//
//		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rgTestsTabLabel, new ToolLauncher(ToolLauncher.rgTestsTabLabel)));

//		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.iigTestsTabLabel, new ToolLauncher(ToolLauncher.iigTestsTabLabel)));

//      xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rigTestsTabLabel, new ToolLauncher(ToolLauncher.rigTestsTabLabel)));

//		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.idsTestsTabLabel, new ToolLauncher(ToolLauncher.idsTestsTabLabel)));

//		xdsTools2AppView.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rsnaedgeTestsTabLabel, new ToolLauncher(ToolLauncher.rsnaedgeTestsTabLabel)));



	}

	int getIndex(ListBox lb, String value) {
		for (int i=0; i<lb.getItemCount(); i++) {
			String lbVal = lb.getItemText(i);
			if (value.equals(lbVal))
				return i;
		}
		return -1;
	}

	public void onTabLoad(TabContainer container, boolean select) {
	}


	public String getWindowShortName() {
		return "home";
	}

	void loadVersion() {

		new GetImplementationVersionCommand(){
			@Override
			public void onComplete(String result) {
				aboutMessage =  "XDS Toolkit\n" + result;
			}
		}.run(getCommandContext());
	}


	public void onTabLoad(TabContainer container, boolean select,
						  String eventName) {

	}




}
