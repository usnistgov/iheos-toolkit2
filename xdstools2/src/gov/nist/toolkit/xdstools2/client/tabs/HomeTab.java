package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab.DirectRegistrationContentValidationTable;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HomeTab extends GenericQueryTab {
	//	private static final String Container = null;
	String aboutMessage = null;	
	String nwhin_flag = "false";
	HorizontalPanel menubar = new HorizontalPanel();


	public HomeTab() {
		super(new GetDocumentsSiteActorManager());
	}


	public void onTabLoad(final Xdstools2 container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();	

		select = true;
		myContainer.addTab(topPanel, "Home", select );

		HTML title = new HTML();
		title.setHTML("<h2>Home</h2>");
		topPanel.add(title);


		HTML docLink = new HTML();
		docLink.setHTML("<a href=\"" + "doc/home.html" + "\" target=\"_blank\">" +  "[help]" + "</a>");
		//		topPanel.add(docLink);

		HTML about = new HTML();
		about.setHTML("<a href=\"" + "doc/about.html" + "\" target=\"_blank\">" +  "[about]" + "</a>");
		//		topPanel.add(docLink);
		
		Hyperlink h = HyperlinkFactory.link("&nbsp;&nbsp;[about version]&nbsp;&nbsp;", new ClickHandler() {

			public void onClick(ClickEvent event) {
				new PopupMessage(aboutMessage);
			}

		});

		menubar.add(docLink);
		menubar.add(h);
		menubar.add(about);

		HTML instLink = new HTML();
		instLink.setHTML("<a href=\"" + "doc/install.html" + "\" target=\"_blank\">" +  "[Installation Instructions]" + "</a>");
		menubar.add(instLink);

		topPanel.add(menubar);

		menubar.add(
				HyperlinkFactory.launchTool("&nbsp;&nbsp;[" + TabLauncher.toolConfigTabLabel + "]&nbsp;&nbsp;", new TabLauncher(myContainer, TabLauncher.toolConfigTabLabel))

				);
		
//		HTML faq = new HTML();
//		faq.setHTML("<a href=\"" + "doc/faq.html" + "\" target=\"_blank\">" +  "[FAQ]" + "</a>");
//		menubar.add(faq);

//		new FeatureManager().addCallback(new MainGridLoader());
		
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
				if ("direct".equals(th)) {
					forDirect = true;
				}
				else {
					forIHE = true;
				}

				if(forDirect) {
					loadDirectGrid(0);
					loadNwHINGrid(1);
					loadIHEGrid(2);
				}

				if (forIHE) {
					loadIHEGrid(0);
				}

				topPanel.add(mainGrid);
				toolkitService.getAdminPassword(getPasswordCallback);
				loadVersion();

				if ("direct".equals(th)) {
					displayDirectHome();
				}

			} catch (Exception e) {
				new PopupMessage(e.getClass().getName() + ": " + e.getMessage());
			}
		}

	}

	void loadDirectGrid(int startingColumn) {
		mainGrid = new FlexTable();
		mainGrid.setCellSpacing(20);


		int row = 0;
		int col = startingColumn;


		// ***************************************************************************
		// Direct

		mainGrid.setWidget(row, col, addHTML("<h2>Direct</h2>"));
		row++;		
		
		
		HTML instLink = new HTML();
		instLink.setHTML("<a href=\"" + "http://healthcare.nist.gov/ttt.html" + "\" target=\"_blank\">" +  "How to use the Direct Tools" + "</a>");
		mainGrid.setWidget(row, col, instLink);
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.directRegistrationTabLabel, new TabLauncher(myContainer, TabLauncher.directRegistrationTabLabel)));
		row++;

		String val = "Message and CCDA document validators";
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(val, new TabLauncher(myContainer, val)));
		row++;


		String val2 = "Send Direct Message";
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(val2, new TabLauncher(myContainer, val2)));
		row++;

		String val3 = "View Direct Message Status";
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(val3, new TabLauncher(myContainer, val3)));
		row++;


		// ***************************************************************************

		//		topPanel.add(mainGrid);
		
		//		toolkitService.getAdminPassword(getPasswordCallback);
		
		//		loadVersion();


	}


	private void displayDirectHome() {
		TkProps pubcertConfig = tkProps().withPrefixRemoved("direct.pubcert");
		String cert = "";
		String trustanchor = "";
		String invtrustrelanchor = "";
		try {
			 cert = pubcertConfig.get("pubcert");
		} catch (PropertyNotFoundException e) {
			new PopupMessage("Configuration parameter direct.pubcert.pubcert cannot be loaded from tk_props.txt properties file located in the external cache");
		}
		try {
			trustanchor = pubcertConfig.get("trustanchor");
		} catch (PropertyNotFoundException e) {
			new PopupMessage("Configuration parameter direct.pubcert.trustanchor cannot be loaded from tk_props.txt properties file located in the external cache");
		}
		try {
			invtrustrelanchor = pubcertConfig.get("invtrustrelanchor");
		} catch (PropertyNotFoundException e) {
			new PopupMessage("Configuration parameter direct.pubcert.invtrustrelanchor cannot be loaded from tk_props.txt properties file located in the external cache");
		}
		
		topPanel.add(new HTML("<hr />"));

		topPanel.add(new HTML("TTT Public Cert can be displayed from <a href=\"pubcert/" + cert + "\">here</a>.  " +
				"The Mime Body of the Direct message must be encrypted with this self-signed Public Cert."));

		topPanel.add(new HTML("<hr />"));

		topPanel.add(new HTML("TTT Trust Anchor can be displayed from <a href=\"pubcert/" + trustanchor + "\">here</a>.  " 
				));
		topPanel.add(new HTML("<hr />"));

		topPanel.add(new HTML("TTT Trust Anchor representing an invalid trust relationship can be displayed from <a href=\"pubcert/" + invtrustrelanchor + "\">here</a>.  " 
				));

		topPanel.add(new HTML("<hr />"));
		
		topPanel.add(new HTML("Human readable CDA/CCR data can be displayed from <a href=\"samples/samples.htm\">here</a>.  " 
				));

		topPanel.add(new HTML("<hr />"));

		DirectRegistrationContentValidationTable drcvTbl = new DirectRegistrationContentValidationTable();
		topPanel.add(drcvTbl.contentValidation());

		topPanel.add(new HTML("<hr />"));

		topPanel.add(new HTML("<h3>Support</h3>"));
		topPanel.add(new HTML("The support mailing list for this tool is: <a href=\"mailto:transport-testing-tool@googlegroups.com\">transport-testing-tool@googlegroups.com</a>"));
		
		topPanel.add(new HTML("...which can be browsed from <a href=\"https://groups.google.com/forum/?fromgroups#!forum/transport-testing-tool\">https://groups.google.com/forum/?fromgroups#!forum/transport-testing-tool</a>"));
		topPanel.add(new HTML("<br />"));
		
		topPanel.add(new HTML("Frequently asked questions (FAQ) (warning - PDF) can be found at: <a href=\"http://www.healthit.gov/sites/default/files/regulation_faqs_11-7-12_0.pdf\">http://www.healthit.gov/sites/default/files/regulation_faqs_11-7-12_0.pdf</a>"));
		topPanel.add(new HTML("<br />"));
		
		topPanel.add(new HTML("Resources available regarding the Direct Protocol Implementation can be found at: <a href=\"http://healthcare.nist.gov/direct.html\">http://healthcare.nist.gov/direct.html</a>"));
		
		
		
		topPanel.add(new HTML("<hr />"));
		
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
			mainGrid.setWidget(row, col, addHTML("<b>Queries & Retrieves</b>"));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.findDocumentsTabLabel)));
			row++;
		}

		if (forIHE) {
			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.mpqFindDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.mpqFindDocumentsTabLabel)));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getDocumentsTabLabel, new TabLauncher(myContainer, TabLauncher.getDocumentsTabLabel)));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getRelatedTabLabel, new TabLauncher(myContainer, TabLauncher.getRelatedTabLabel)));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.findFoldersTabLabel, new TabLauncher(myContainer, TabLauncher.findFoldersTabLabel)));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getFoldersTabLabel, new TabLauncher(myContainer, TabLauncher.getFoldersTabLabel)));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getFolderAndContentsTabLabel, new TabLauncher(myContainer, TabLauncher.getFolderAndContentsTabLabel)));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.getSubmissionSetTabLabel, new TabLauncher(myContainer, TabLauncher.getSubmissionSetTabLabel)));
			row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.documentRetrieveTabLabel, new TabLauncher(myContainer, TabLauncher.documentRetrieveTabLabel)));
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

		mainGrid.setWidget(row, col, addHTML("<b>Send Test Data</b>"));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.registryTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.registryTestDataTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.repositoryTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.repositoryTestDataTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.recipientTestDataTabLabel, new TabLauncher(myContainer, TabLauncher.recipientTestDataTabLabel)));
		row++;



		// ***************************************************************************
		// Tools

		row=0;
		col=startingColumn+2;

		mainGrid.setWidget(row, col, addHTML("<b>Tools</b>"));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.adminTabLabel, new TabLauncher(myContainer, TabLauncher.adminTabLabel)));
		row++;

		if (forIHE) {
			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.repositoryTabLabel, new TabLauncher(myContainer, TabLauncher.repositoryTabLabel)));
			row++;
		}

		if (forIHE) {
			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.mesaTabLabel, new TabLauncher(myContainer, TabLauncher.mesaTabLabel)));
			row++;

			if (Xdstools2.tkProps().get("toolkit.mainmenu.experimental", "false").equalsIgnoreCase("true")) {
				mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.testRunnerTabLabel, new TabLauncher(myContainer, TabLauncher.testRunnerTabLabel)));
				row++;
			}

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.testLogLabel, new TabLauncher(myContainer, TabLauncher.testLogLabel)));
			row++;

			//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.allocatePatientIdTabLabel, new TabLauncher(container, TabLauncher.allocatePatientIdTabLabel)));
			//		row++;

			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.connectathonTabLabel, new TabLauncher(myContainer, TabLauncher.connectathonTabLabel)));
			row++;
		}

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.messageValidatorTabLabel, new TabLauncher(myContainer, TabLauncher.messageValidatorTabLabel)));
		row++;

		// ***************************************************************************
		// Simulators

		row=0;
		col=startingColumn+3;

		mainGrid.setWidget(row, col, addHTML("<b>Simulators</b>"));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.simulatorControlTabLabel, new TabLauncher(myContainer, TabLauncher.simulatorControlTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.simulatorMessageViewTabLabel, new TabLauncher(myContainer, TabLauncher.simulatorMessageViewTabLabel)));
		row++;


		// ***************************************************************************


	}


	//	private void loadDirectGrid(int startingColumn) {
	//		int row;
	//		int col;
	//		// ***************************************************************************
	//		// Direct
	//
	//		row=0;
	//		col=startingColumn+5;
	//
	//		mainGrid.setWidget(row, col, addHTML("<b>Direct</b>"));
	//		row++;		
	//
	//		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.directRegistrationTabLabel, new TabLauncher(myContainer, TabLauncher.directRegistrationTabLabel)));
	//		row++;
	//	}


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
		// TODO Auto-generated method stub

	}




}
