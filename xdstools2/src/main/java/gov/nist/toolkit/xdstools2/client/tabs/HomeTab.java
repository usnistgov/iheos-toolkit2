package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

public class HomeTab extends GenericQueryTab {
	String aboutMessage = null;
	HorizontalFlowPanel menubar = new HorizontalFlowPanel();


	public HomeTab() {
		super(new FindDocumentsSiteActorManager(),"");
//		super(new GetDocumentsSiteActorManager());
	}


	@Override
	protected Widget buildUI() {
		return null;
	}

	@Override
	protected void bindUI() {

	}

	@Override
	protected void configureTabView() {

	}

	@Override
//	public void onTabLoad(final Xdstools2 container, boolean select, String eventName) {
	public void onTabLoad(boolean select, String eventName) {

		addActorReloader();

		select = true;
		registerTab(select, eventName);
//		tabTopPanel.add(new HTML("Menu Bar"));
		tabTopPanel.add(menubar);

		menubar.add(
				HyperlinkFactory.launchTool("&nbsp;&nbsp;[" + ToolLauncher.toolConfigTabLabel + "]&nbsp;&nbsp;", new ToolLauncher(ToolLauncher.toolConfigTabLabel))
		);

		Frame frame = new Frame("site/index.html");
		frame.setSize("100em", "100em");
		tabTopPanel.add(frame);

		new MainGridLoader().featuresLoadedCallback();
	}

	boolean forDirect = false;
	boolean forIHE = false;
	boolean forNwHIN = false;

	class MainGridLoader {

		//@Override
		public void featuresLoadedCallback() {
			String th = "";

			try {
				th = Xdstools2.tkProps().get("toolkit.home","");
			} catch (Throwable t) {

			}

			mainGrid = new FlexTable();
			mainGrid.setCellSpacing(20);

			loadIHEGrid(0);

//				tabTopPanel.add(mainGrid);
			toolkitService.getAdminPassword(getPasswordCallback);
			loadVersion();

		}

	}

	private void loadIHEGrid(int startingColumn) {


		// ************************************************************************	
		// Queries and Retrieves

		int row = 0;
		int col = startingColumn;
		Xdstools2.clearMainMenu();

		Xdstools2.addtoMainMenu(addHTML("<h2>Toolkit</h2>"));

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.homeTabLabel, new ToolLauncher(ToolLauncher.homeTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(addHTML("<h3>Queries & Retrieves</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsTabLabel, new ToolLauncher(ToolLauncher.findDocumentsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsAllParametersTabLabel, new ToolLauncher(ToolLauncher.findDocumentsAllParametersTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsByRefIdTabLabel, new ToolLauncher(ToolLauncher.findDocumentsByRefIdTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mpqFindDocumentsTabLabel, new ToolLauncher(ToolLauncher.mpqFindDocumentsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getDocumentsTabLabel, new ToolLauncher(ToolLauncher.getDocumentsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getRelatedTabLabel, new ToolLauncher(ToolLauncher.getRelatedTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findFoldersTabLabel, new ToolLauncher(ToolLauncher.findFoldersTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFoldersTabLabel, new ToolLauncher(ToolLauncher.getFoldersTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFolderAndContentsTabLabel, new ToolLauncher(ToolLauncher.getFolderAndContentsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getSubmissionSetTabLabel, new ToolLauncher(ToolLauncher.getSubmissionSetTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getAllTabLabel, new ToolLauncher(ToolLauncher.getAllTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.documentRetrieveTabLabel, new ToolLauncher(ToolLauncher.documentRetrieveTabLabel)));
		row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(ToolLauncher.imagingDocumentSetRetrieveTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(ToolLauncher.imagingDocumentSetRetrieveTabLabel)));
		row++;

		// ***************************************************************************
		// Test data

		row=0;
		col=startingColumn+1;

		Xdstools2.addtoMainMenu(addHTML("<h3>Submit</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.registryTestDataTabLabel, new ToolLauncher(ToolLauncher.registryTestDataTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.repositoryTestDataTabLabel, new ToolLauncher(ToolLauncher.repositoryTestDataTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.recipientTestDataTabLabel, new ToolLauncher(ToolLauncher.recipientTestDataTabLabel)));
		row++;



		// ***************************************************************************
		// Tools

		row=0;
		col=startingColumn+2;

		Xdstools2.addtoMainMenu(addHTML("<h3>Tools</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.pidFavoritesLabel, new ToolLauncher(ToolLauncher.pidFavoritesLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.adminTabLabel, new ToolLauncher(ToolLauncher.adminTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.repositoryTabLabel, new ToolLauncher(ToolLauncher.repositoryTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.testLogLabel, new ToolLauncher(ToolLauncher.testLogLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.connectathonTabLabel, new ToolLauncher(ToolLauncher.connectathonTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.messageValidatorTabLabel, new ToolLauncher(ToolLauncher.messageValidatorTabLabel)));
		row++;

		// ***************************************************************************
		// Tests

		Xdstools2.addtoMainMenu(addHTML("<h3>Tests</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mesaTabLabel, new ToolLauncher(ToolLauncher.mesaTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.igTestsTabLabel, new ToolLauncher(ToolLauncher.igTestsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rgTestsTabLabel, new ToolLauncher(ToolLauncher.rgTestsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.iigTestsTabLabel, new ToolLauncher(ToolLauncher.iigTestsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.idsTestsTabLabel, new ToolLauncher(ToolLauncher.idsTestsTabLabel)));
		row++;

		// ***************************************************************************
		// Simulators

		row=0;
		col=startingColumn+3;

		Xdstools2.addtoMainMenu(addHTML("<h3>Simulators</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorControlTabLabel, new ToolLauncher(ToolLauncher.simulatorControlTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorMessageViewTabLabel, new ToolLauncher(ToolLauncher.simulatorMessageViewTabLabel)));
		row++;


		// ***************************************************************************


	}

	int getIndex(ListBox lb, String value) {
		for (int i=0; i<lb.getItemCount(); i++) {
			String lbVal = lb.getItemText(i);
			if (value.equals(lbVal))
				return i;
		}
		return -1;
	}

	AsyncCallback<String> getPasswordCallback = new AsyncCallback<String> () {

		public void onFailure(Throwable caught) {
			new PopupMessage("Call to retrieve admin password failed: " + caught.getMessage());
		}

		public void onSuccess(String result) {
			PasswordManagement.adminPassword = result;
		}

	};

	public void onTabLoad(TabContainer container, boolean select) {
	}


	public String getWindowShortName() {
		return "home";
	}

	void loadVersion() {

		toolkitService.getImplementationVersion(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				aboutMessage =  caught.getMessage();
				new PopupMessage("Cannot load the implementation version - " +
						" This is usually cased by an error in building the WAR file. " +
						aboutMessage);
			}

			public void onSuccess(String result) {
				aboutMessage =  "XDS Toolkit\n" + result;
			}

		});
	}


	public void onTabLoad(TabContainer container, boolean select,
						  String eventName) {

	}




}
