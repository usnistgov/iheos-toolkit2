package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

public class HomeTab extends GenericQueryTab {
	String aboutMessage = null;
	String nwhin_flag = "false";
	HorizontalPanel menubar = new HorizontalPanel();


	public HomeTab() {
		super(new FindDocumentsSiteActorManager());
	}


	public void onTabLoad(final Xdstools2 container, boolean select, String eventName) {
		myContainer = container;

		addActorReloader();

		topPanel = new VerticalPanel();

		select = true;
		myContainer.addTab(topPanel, "Home", select);
		topPanel.add(menubar);

		menubar.add(
				HyperlinkFactory.launchTool("&nbsp;&nbsp;[" + ToolLauncher.toolConfigTabLabel + "]&nbsp;&nbsp;", new ToolLauncher(myContainer, ToolLauncher.toolConfigTabLabel))

		);

		Frame frame = new Frame("site/index.html");
		frame.setSize("100em", "100em");
		topPanel.add(frame);

		new MainGridLoader().featuresLoadedCallback();
	}

	boolean forIHE = false;

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

			try {
				forIHE = true;

				if (forIHE) {
					loadIHEGrid(0);
				}

				topPanel.add(mainGrid);
				toolkitService.getAdminPassword(getPasswordCallback);
				loadVersion();

			} catch (Exception e) {
				new PopupMessage(e.getClass().getName() + ": " + e.getMessage());
			}
		}

	}

	void loadIHEGrid(int startingColumn) {


		// ************************************************************************	
		// Queries and Retrieves

		int row = 0;
		int col = startingColumn;

		if (forIHE) {
			Xdstools2.addtoMainMenu(addHTML("<h2>Toolkit</h2>"));
			Xdstools2.addtoMainMenu(addHTML("<h3>Queries & Retrieves</h3>"));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsTabLabel, new ToolLauncher(myContainer, ToolLauncher.findDocumentsTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsAllParametersTabLabel, new ToolLauncher(myContainer, ToolLauncher.findDocumentsAllParametersTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsByRefIdTabLabel, new ToolLauncher(myContainer, ToolLauncher.findDocumentsByRefIdTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mpqFindDocumentsTabLabel, new ToolLauncher(myContainer, ToolLauncher.mpqFindDocumentsTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getDocumentsTabLabel, new ToolLauncher(myContainer, ToolLauncher.getDocumentsTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getRelatedTabLabel, new ToolLauncher(myContainer, ToolLauncher.getRelatedTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findFoldersTabLabel, new ToolLauncher(myContainer, ToolLauncher.findFoldersTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFoldersTabLabel, new ToolLauncher(myContainer, ToolLauncher.getFoldersTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFolderAndContentsTabLabel, new ToolLauncher(myContainer, ToolLauncher.getFolderAndContentsTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getSubmissionSetTabLabel, new ToolLauncher(myContainer, ToolLauncher.getSubmissionSetTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getAllTabLabel, new ToolLauncher(myContainer, ToolLauncher.getAllTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.documentRetrieveTabLabel, new ToolLauncher(myContainer, ToolLauncher.documentRetrieveTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(myContainer, ToolLauncher.imagingDocumentSetRetrieveTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(myContainer, ToolLauncher.imagingDocumentSetRetrieveTabLabel)));
			row++;



		}

		// ***************************************************************************
		// Test data

		row=0;
		col=startingColumn+1;

		Xdstools2.addtoMainMenu(addHTML("<h3>Submit</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.registryTestDataTabLabel, new ToolLauncher(myContainer, ToolLauncher.registryTestDataTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.repositoryTestDataTabLabel, new ToolLauncher(myContainer, ToolLauncher.repositoryTestDataTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.recipientTestDataTabLabel, new ToolLauncher(myContainer, ToolLauncher.recipientTestDataTabLabel)));
		row++;



		// ***************************************************************************
		// Tools

		row=0;
		col=startingColumn+2;

		Xdstools2.addtoMainMenu(addHTML("<h3>Tools</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.pidFavoritesLabel, new ToolLauncher(myContainer, ToolLauncher.pidFavoritesLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.sitesTabLabel, new ToolLauncher(myContainer, ToolLauncher.sitesTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.repositoryTabLabel, new ToolLauncher(myContainer, ToolLauncher.repositoryTabLabel)));
		row++;

		if (Xdstools2.tkProps().get("toolkit.mainmenu.experimental", "false").equalsIgnoreCase("true")) {
			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.testRunnerTabLabel, new ToolLauncher(myContainer, ToolLauncher.testRunnerTabLabel)));
			row++;
		}

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.testLogLabel, new ToolLauncher(myContainer, ToolLauncher.testLogLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.connectathonTabLabel, new ToolLauncher(myContainer, ToolLauncher.connectathonTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.messageValidatorTabLabel, new ToolLauncher(myContainer, ToolLauncher.messageValidatorTabLabel)));
		row++;

		// ***************************************************************************
		// Tests

		Xdstools2.addtoMainMenu(addHTML("<h3>Tests</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mesaTabLabel, new ToolLauncher(myContainer, ToolLauncher.mesaTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.igTestsTabLabel, new ToolLauncher(myContainer, ToolLauncher.igTestsTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rgTestsTabLabel, new ToolLauncher(myContainer, ToolLauncher.rgTestsTabLabel)));
		row++;

		// ***************************************************************************
		// Simulators

		row=0;
		col=startingColumn+3;

		Xdstools2.addtoMainMenu(addHTML("<h3>Simulators</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorControlTabLabel, new ToolLauncher(myContainer, ToolLauncher.simulatorControlTabLabel)));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorMessageViewTabLabel, new ToolLauncher(myContainer, ToolLauncher.simulatorMessageViewTabLabel)));
		row++;


		// ***************************************************************************


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


	@Override
	public void onTabLoad(TabContainer container, boolean select,
						  String eventName) {

	}




}
