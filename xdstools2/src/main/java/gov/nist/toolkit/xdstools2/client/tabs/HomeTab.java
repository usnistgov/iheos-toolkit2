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
import gov.nist.toolkit.xdstools2.client.resources.HomePageResources;

public class HomeTab extends GenericQueryTab {
	//	private static final String Container = null;
	String aboutMessage = null;	
	String nwhin_flag = "false";
	HorizontalPanel menubar = new HorizontalPanel();


	public HomeTab() {
		super(new FindDocumentsSiteActorManager());
//		super(new GetDocumentsSiteActorManager());
	}


	public void onTabLoad(final Xdstools2 container, boolean select, String eventName) {
		myContainer = container;

		addActorReloader();

		topPanel = new VerticalPanel();	

		select = true;
		myContainer.addTab(topPanel, "Home", select);

//		HTML title = new HTML();
//		title.setHTML("<h2>Home</h2>");
//		topPanel.add(title);

//		HTML docLink = new HTML();
//		docLink.setHTML("<a href=\"" + "site/home.html" + "\" target=\"_blank\">" +  "[help]" + "</a>");
		//		topPanel.add(docLink);

//		HTML about = new HTML();
//		about.setHTML("<a href=\"" + "site/about.html" + "\" target=\"_blank\">" +  "[about]" + "</a>");
		//		topPanel.add(docLink);
		
//		Hyperlink h = HyperlinkFactory.link("&nbsp;&nbsp;[about version]&nbsp;&nbsp;", new ClickHandler() {
//
//			public void onClick(ClickEvent event) {
//				new PopupMessage(aboutMessage);
//			}
//
//		});

//		menubar.add(docLink);
//		menubar.add(h);
//		menubar.add(about);

//		HTML instLink = new HTML();
//		instLink.setHTML("<a href=\"" + "site/install.html" + "\" target=\"_blank\">" +  "[Installation Instructions]" + "</a>");
//		menubar.add(instLink);

		topPanel.add(menubar);

		menubar.add(
				HyperlinkFactory.launchTool("&nbsp;&nbsp;[" + TabLauncher.toolConfigTabLabel + "]&nbsp;&nbsp;", new TabLauncher(myContainer, TabLauncher.toolConfigTabLabel))

				);

//		HTML faq = new HTML();
//		faq.setHTML("<a href=\"" + "doc/faq.html" + "\" target=\"_blank\">" +  "[FAQ]" + "</a>");
//		menubar.add(faq);

//		new FeatureManager().addCallback(new MainGridLoader());

//		topPanel.add(new HTML("<a href=\"doc/howto/index.html\">How to...</a>"));

//		HTML howtoPanel = new HTML();
//		String html = HomePageResources.INSTANCE.getIntroHtml().getText();
//		howtoPanel.setHTML(html);
//		topPanel.add(howtoPanel);

        Frame frame = new Frame("site/index.html");
        frame.setSize("100em", "100em");
        topPanel.add(frame);

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

	void loadCCDAGrid() {
		mainGrid = new FlexTable();
		mainGrid.setCellSpacing(20);


		int row = 0;
		int col = 0;


		// ***************************************************************************
		// Direct

		mainGrid.setWidget(row, col, addHTML("<h2>Direct</h2>"));
		row++;		

		String val = "Message and CCDA document validators";
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(val, new TabLauncher(myContainer, val)));
		row++;

		// ***************************************************************************

		topPanel.add(mainGrid);

		toolkitService.getAdminPassword(getPasswordCallback);

		loadVersion();


	}

	void loadIHEGrid(int startingColumn) {


		// ************************************************************************	
		// Queries and Retrieves

		int row = 0;
		int col = startingColumn;

		if (forIHE) {
//			mainGrid.setWidget(row, col, addHTML("<b>Queries & Retrieves</b>"));
			Xdstools2.addtoMainMenu(addHTML("<h2>Toolkit</h2>"));
			Xdstools2.addtoMainMenu(addHTML("<h3>Queries & Retrieves</h3>"));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.findDocumentsTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.findDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.findDocumentsTabLabel)));
			row++;

			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.findDocumentsAllParametersTabLabel, new TabLauncher(myContainer, TabLauncher.findDocumentsAllParametersTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findDocumentsByRefIdTabLabel, new TabLauncher(myContainer, TabLauncher.findDocumentsByRefIdTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.findDocumentsByRefIdTabLabel, new TabLauncher(myContainer, TabLauncher.findDocumentsByRefIdTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.mpqFindDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.mpqFindDocumentsTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.mpqFindDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.mpqFindDocumentsTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.getDocumentsTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.getDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.getDocumentsTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getRelatedTabLabel, new TabLauncher(myContainer, TabLauncher.getRelatedTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.getRelatedTabLabel, new TabLauncher(myContainer, TabLauncher.getRelatedTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findFoldersTabLabel, new TabLauncher(myContainer, TabLauncher.findFoldersTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.findFoldersTabLabel, new TabLauncher(myContainer, TabLauncher.findFoldersTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getFoldersTabLabel, new TabLauncher(myContainer, TabLauncher.getFoldersTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.getFoldersTabLabel, new TabLauncher(myContainer, TabLauncher.getFoldersTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getFolderAndContentsTabLabel, new TabLauncher(myContainer, TabLauncher.getFolderAndContentsTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.getFolderAndContentsTabLabel, new TabLauncher(myContainer, TabLauncher.getFolderAndContentsTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getSubmissionSetTabLabel, new TabLauncher(myContainer, TabLauncher.getSubmissionSetTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.getSubmissionSetTabLabel, new TabLauncher(myContainer, TabLauncher.getSubmissionSetTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getAllTabLabel, new TabLauncher(myContainer, TabLauncher.getAllTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.getAllTabLabel, new TabLauncher(myContainer, TabLauncher.getAllTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.documentRetrieveTabLabel, new TabLauncher(myContainer, TabLauncher.documentRetrieveTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.documentRetrieveTabLabel, new TabLauncher(myContainer, TabLauncher.documentRetrieveTabLabel)));
			row++;
		}

		if (forNwHIN) {
			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findPatientTabLabel, new TabLauncher(myContainer, TabLauncher.findPatientTabLabel)));
			row++;
		}

		// ***************************************************************************
		// Test data

		row=0;
		col=startingColumn+1;

//		mainGrid.setWidget(row, col, addHTML("<b>Submit</b>"));
		Xdstools2.addtoMainMenu(addHTML("<h3>Submit</h3>"));
		row++;

//		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.registryTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.registryTestDataTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.link(TabLauncher.registryTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.registryTestDataTabLabel)));
		row++;

//		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.repositoryTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.repositoryTestDataTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.link(TabLauncher.repositoryTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.repositoryTestDataTabLabel)));
		row++;

//		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.recipientTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.recipientTestDataTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.link(TabLauncher.recipientTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.recipientTestDataTabLabel)));
		row++;



		// ***************************************************************************
		// Tools

		row=0;
		col=startingColumn+2;

//		mainGrid.setWidget(row, col, addHTML("<b>Tools</b>"));
		Xdstools2.addtoMainMenu(addHTML("<h3>Tools</h3>"));
		row++;

		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.testsOverviewTabLabel, new TabLauncher(myContainer, TabLauncher.testsOverviewTabLabel)));
		row++;

//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.pidFavoritesLabel, new TabLauncher(myContainer, TabLauncher.pidFavoritesLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.pidFavoritesLabel, new TabLauncher(myContainer, TabLauncher.pidFavoritesLabel)));
		row++;

//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.adminTabLabel, new TabLauncher(myContainer, TabLauncher.adminTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.adminTabLabel, new TabLauncher(myContainer, TabLauncher.adminTabLabel)));
		row++;

		if (forIHE) {
//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.repositoryTabLabel, new TabLauncher(myContainer, TabLauncher.repositoryTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.repositoryTabLabel, new TabLauncher(myContainer, TabLauncher.repositoryTabLabel)));
			row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.mesaTabLabel, new TabLauncher(myContainer, TabLauncher.mesaTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.mesaTabLabel, new TabLauncher(myContainer, TabLauncher.mesaTabLabel)));
			row++;

			if (Xdstools2.tkProps().get("toolkit.mainmenu.experimental", "false").equalsIgnoreCase("true")) {
				mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.testRunnerTabLabel, new TabLauncher(myContainer, TabLauncher.testRunnerTabLabel)));
				row++;
			}

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.testLogLabel, new TabLauncher(myContainer, TabLauncher.testLogLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.testLogLabel, new TabLauncher(myContainer, TabLauncher.testLogLabel)));
			row++;

			//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.allocatePatientIdTabLabel, new TabLauncher(container, TabLauncher.allocatePatientIdTabLabel)));
			//		row++;

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.connectathonTabLabel, new TabLauncher(myContainer, TabLauncher.connectathonTabLabel)));
			Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.connectathonTabLabel, new TabLauncher(myContainer, TabLauncher.connectathonTabLabel)));
			row++;
		}

//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.messageValidatorTabLabel, new TabLauncher(myContainer, TabLauncher.messageValidatorTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.messageValidatorTabLabel, new TabLauncher(myContainer, TabLauncher.messageValidatorTabLabel)));
		row++;

		// ***************************************************************************
		// Simulators

		row=0;
		col=startingColumn+3;

//		mainGrid.setWidget(row, col, addHTML("<b>Simulators</b>"));
		Xdstools2.addtoMainMenu(addHTML("<h3>Simulators</h3>"));
		row++;

//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.simulatorControlTabLabel, new TabLauncher(myContainer, TabLauncher.simulatorControlTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.simulatorControlTabLabel, new TabLauncher(myContainer, TabLauncher.simulatorControlTabLabel)));
		row++;

//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.simulatorMessageViewTabLabel, new TabLauncher(myContainer, TabLauncher.simulatorMessageViewTabLabel)));
		Xdstools2.addtoMainMenu(HyperlinkFactory.launchTool(TabLauncher.simulatorMessageViewTabLabel, new TabLauncher(myContainer, TabLauncher.simulatorMessageViewTabLabel)));
		row++;


		// ***************************************************************************


	}

	private void loadNwHINGrid(int startingColumn) {
		int row;
		int col;
		// ***************************************************************************
		// NwHIN


		row=0;
		col=startingColumn;

		mainGrid.setWidget(row, col, addHTML("<b>NwHIN</b>"));
		row++;		

		//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findPatientTabLabel, new TabLauncher(myContainer, TabLauncher.findPatientTabLabel)));
		//		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.findDocumentsTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.getDocumentsTabLabel)));
		row++;

		//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.messageValidatorTabLabel, new TabLauncher(myContainer, TabLauncher.messageValidatorTabLabel)));
		//		row++;
		//
		//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.nwhinTabLabel, new TabLauncher(myContainer, TabLauncher.nwhinTabLabel)));
		//		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.documentRetrieveTabLabel, new TabLauncher(myContainer, TabLauncher.documentRetrieveTabLabel)));
		row++;

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


	@Override
	public void onTabLoad(TabContainer container, boolean select,
			String eventName) {

	}




}
