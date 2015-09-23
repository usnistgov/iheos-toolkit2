package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.registrymetadata.client.*;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Infrastructure for any tab that will allow a site to be chosen,
 * issue a transaction, get back results, 
 * and allow the results to be inspected
 * @author bill
 *
 */
public abstract class GenericQueryTab  extends TabbedWindow {
	private final SiteLoader siteLoader = new SiteLoader(this);
	GenericQueryTab me;

	protected FlexTable mainGrid;
	public int row_initial;
	int row;

	public boolean tlsEnabled = true;
	public boolean samlEnabled = true;
	ActorType selectByActor = null;
	List<TransactionType> transactionTypes;
	public TransactionSelectionManager transactionSelectionManager = null;
	public boolean enableInspectResults = true;
	CoupledTransactions couplings;
	public boolean runEnabled = true;
	ClickHandler runner;
	String runButtonText = "Run";

	public VerticalPanel resultPanel = new VerticalPanel();
	public TabContainer myContainer;
	CheckBox doTls = new CheckBox("TLS?");
	ListBox samlListBox = new ListBox();
	List<RadioButton> byActorButtons = null;
	//	public Map<TransactionType, List<RadioButton>> perTransTypeRadioButtons;

	List<Result> results;
	private Button inspectButton;
	private Button goButton;

	boolean showInspectButton = true;
	boolean asyncEnabled = false;
	public boolean doASYNC = false;
	BaseSiteActorManager siteActorManager;// = new SiteActorManager(this);
	boolean hasPatientIdParam = false;
	HTML resultsShortDescription = new HTML();

	static TransactionOfferings transactionOfferings = null;  // Loaded from server

	protected QueryBoilerplate queryBoilerplate = null;


	HTML statusBox = new HTML();
	public TextBox pidTextBox;


	public GenericQueryTab(BaseSiteActorManager siteActorManager) {
		me = this;
		this.siteActorManager = siteActorManager;
		siteActorManager.setGenericQueryTab(this);

		// when called as HomeTab is built, the wrong session services this call, this
		// makes sure the job gets done
		//		EnvironmentSelector.SETENVIRONMENT(toolkitService);
	}

	protected TestSessionManager2 getTestSessionManager() { return testSessionManager; }

	public void setTlsEnabled(boolean value) { tlsEnabled = value; }
	public void setSamlEnabled(boolean value) { samlEnabled = value; }
	public void setShowInspectButton(boolean value) {
		showInspectButton = value;
		if (inspectButton != null)
			inspectButton.setVisible(showInspectButton);
	}

	public boolean isTLS() {
		return doTls.getValue();
	}

	public boolean isSaml() {
		int selection = samlListBox.getSelectedIndex();
		if (selection == 1)
			return true;  // first selection must be no saml
		return false;
	}

	protected AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {


		public void onFailure(Throwable caught) {
//			resultPanel.clear();
			resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error running validation: " + caught.getMessage() + "</font>"));
			resultsShortDescription.setText("");
		}

		public void onSuccess(List<Result> theresult) {
			resultsShortDescription.setText("");
			try {
				if (theresult.size() > 0) {
					MetadataCollection mc = theresult.get(0).getStepResults().get(0).getMetadata();
					StringBuilder buf = new StringBuilder();
					buf.append("  ==> ");
					buf.append(mc.submissionSets.size()).append(" SubmissionSets ");
					buf.append(mc.docEntries.size()).append(" DocumentEntries ");
					buf.append(mc.folders.size()).append(" Folders ");
					buf.append(mc.objectRefs.size()).append(" ObjectRefs ");
					resultsShortDescription.setText(buf.toString());
				}
			} catch (Exception e) {}
			boolean status = true;
			results = theresult;
			for (Result result : results) {
				for (AssertionResult ar : result.assertions.assertions) {
					String assertion = ar.assertion.replaceAll("\n", "<br />");
					if (ar.status) {
						resultPanel.add(addHTML(assertion));
					} else {
						if (assertion.contains("EnvironmentNotSelectedException"))
							resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Environment Not Selected" + "</font>"));
						else
							resultPanel.add(addHTML("<font color=\"#FF0000\">" + assertion + "</font>"));
						status = false;
					}
				}
			}
			if (status)
				setStatus("Status: Success", true);
			else
				setStatus("Status: Failure", false);
			getInspectButton().setEnabled(true);
			getGoButton().setEnabled(true);
		}

	};



	public void tabIsSelected() { 
		System.out.println("tab selected: " + getCommonSiteSpec());

		doTls.setValue(getCommonSiteSpec().isTls());
		samlListBox.setSelectedIndex((getCommonSiteSpec().isSaml) ? 1 : 0);
		if (pidTextBox != null)
			pidTextBox.setText(getCommonPatientId());

		//		String defaultName = defaultSiteSpec.getName();
		//		for (RadioButton rb : byActorButtons) {
		//			String name = rb.getName();
		//			if (defaultName.equals(name)) rb.setValue(true);
		//		}

	}

	public void setSiteSpec(SiteSpec siteSpec) { setCommonSiteSpec(siteSpec); }

	//	protected SiteSpec verifySiteSelection() {
	//		setCommonSiteSpec(siteActorManager.verifySiteSelection());
	//		return getCommonSiteSpec();
	//	}

	public QueryBoilerplate getQueryBoilerplate() {
		return queryBoilerplate;
	}

	//
	// These three versions of addQueryBoilerplate should be made into static methods
	//  probably hung off a QueryBoilerplateFactory class
	protected QueryBoilerplate addQueryBoilerplate(ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings, ActorType selectByActor) {
		if (queryBoilerplate != null) {
			queryBoilerplate.remove();
			queryBoilerplate = null;
		}
		queryBoilerplate = new QueryBoilerplate(
				this, runner, transactionTypes,
				couplings, selectByActor
				);
		return queryBoilerplate;
	}

	protected QueryBoilerplate addQueryBoilerplate(ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings) {
		if (queryBoilerplate != null) {
			queryBoilerplate.remove();
			queryBoilerplate = null;
		}
		queryBoilerplate = new QueryBoilerplate(
				this, runner, transactionTypes,
				couplings
				);
		return queryBoilerplate;

	}

	protected QueryBoilerplate addQueryBoilerplate(ClickHandler runner, List<TransactionType> transactionTypes, 
			CoupledTransactions couplings, boolean hasPatientIdParam) {
		if (queryBoilerplate != null) {
			queryBoilerplate.remove();
			queryBoilerplate = null;
		}
		this.hasPatientIdParam = hasPatientIdParam;

		if (mainConfigPanelDivider == null) {
			mainConfigPanelDivider = new HTML("<hr />");
			topPanel.add(mainConfigPanelDivider);
			mainConfigPanel = new VerticalPanel();
			topPanel.add(mainConfigPanel);
			topPanel.add(new HTML("<hr />"));
		}
		topPanel.add(resultPanel);
		queryBoilerplate = new QueryBoilerplate(
				this, runner, transactionTypes,
				couplings
				);
		return queryBoilerplate;
	}

	public String getSelectedValueFromListBox(ListBox lb) {
		int i = lb.getSelectedIndex();
		if ( i == -1)
			return null;
		return lb.getValue(i);
	}

	static public HTML addHTML(String html) {
		HTML msgBox = new HTML();
		msgBox.setHTML(html);
		return msgBox;		
	}

	HTML addText(String text) {		
		HTML msgBox = new HTML();
		msgBox.setText(text);
		return msgBox;		
	}

	public void addHtmlResults(String html) {
		resultPanel.add(addHTML(html));
	}

	public void addTextResults(String text) {
		resultPanel.add(addText(text));
	}

	protected void showMessage(Throwable caught) {
		showMessage(caught.getMessage());
	}

	protected void showMessage(String message) {		
		HTML msgBox = new HTML();
		msgBox.setHTML("<b>" + message + "</b>");
		topPanel.add(msgBox);		
	}

	protected List<String> formatIds(String value) {
		List<String> values = new ArrayList<String>();

		String[] parts = value.split("[,;() \t\n\r']");

		for (int i=0; i<parts.length; i++) {
			String v = parts[i];
			if (v != null) {
				v = v.trim();
				if (!v.equals(""))
					values.add(v);
			}
		}

		return values;
	}

	// all UUIDs or all UIDs
	protected boolean verifyUuids(List<String> ids) {
		if (ids.size() == 0)
			return true;
		boolean isUUID = (ids.get(0).startsWith("urn:uuid:"));
		for (String id : ids) {
			if (id.startsWith("urn:uuid:") != isUUID)
				return false;
		}
		return true;
	}

	protected ObjectRefs getObjectRefs(List<String> ids) {
		ObjectRefs or = new ObjectRefs();

		for (String id : ids) {
			or.objectRefs.add(new ObjectRef(id));
		}

		return or;
	}

	protected AnyIds getAnyIds(List<String> ids) {
		AnyIds aids = new AnyIds();

		for (String id : ids) {
			aids.add(new AnyId(id));
		}
		return aids;
	}

	String red(String msg, boolean status) {
		if (status)
			return msg;
		return HtmlMarkup.red(msg);
	}

	public void setStatus(String message, boolean status) {
		statusBox.setHTML(HtmlMarkup.bold(red(message,status)));
	}

	public String getRunningMessage() {
		return "Running (connection timeout is 30 sec) ...";
	}

	public void addStatusBox() {
		addStatusBox(getRunningMessage());
	}

	public void addStatusBox(String initialMessage) {
		setStatus(initialMessage, true);
		resultPanel.add(statusBox);
	}

	public Button getGoButton() {
		return goButton;
	}

	public void setGoButton(Button goButton) {
		this.goButton = goButton;
	}

	public Button getInspectButton() {
		return inspectButton;
	}

	public void setInspectButton(Button inspectButton) {
		this.inspectButton = inspectButton;
		inspectButton.setVisible(showInspectButton);
	}

	Anchor reload = null;

	public void addActorReloader() {
		if (reload == null) {
			reload = new Anchor();
			reload.setTitle("Reload actors configuration");
			reload.setText("[reload]");
			me.addToMenu(reload);

			reload.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					reloadTransactionOfferings();
				}

			});
			reload.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					onReload();
				}

			});
		}
	}

	// so it can be overloaded
	public void onReload() {}

	void reloadTransactionOfferings() {
		try {
			toolkitService.getTransactionOfferings(new AsyncCallback<TransactionOfferings> () {

				public void onFailure(Throwable caught) {
					resultPanel.clear();
					resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error: " + caught.getMessage() + "</font>"));
				}

				public void onSuccess(TransactionOfferings to) {
					GenericQueryTab.transactionOfferings = to;
					redisplay(false);
				}

			});
		} catch (Exception e) {
			resultPanel.clear();
			resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error: " + e.getMessage() + "</font>"));
		}
	}

	// clean out mainGrid so the actors can be re-added
	public void initMainGrid() {
		if (mainGrid == null) {
			mainGrid = new FlexTable();
			topPanel.add(mainGrid);
		}
		while (mainGrid.getRowCount() > row_initial)
			mainGrid.removeRow(mainGrid.getRowCount() - 1);
		row = row_initial;
	}

	Widget mainConfigPanelDivider = null;
	VerticalPanel mainConfigPanel = null;

	public void redisplay(boolean clearResults) {

		if (resultPanel != null && clearResults)
			resultPanel.clear();
		initMainGrid();

		//			genericQueryTab.perTransTypeRadioButtons = new HashMap<TransactionType, List<RadioButton>>();

		mainConfigPanel.clear();


		// two columns - title and contents
		final int titleColumn = 0;
		final int contentsColumn = 1;
		int commonGridRow = 0;
		FlexTable commonParamGrid = new FlexTable();
		mainConfigPanel.add(commonParamGrid);

		if (hasPatientIdParam) {
			commonParamGrid.setWidget(commonGridRow, titleColumn, new HTML("Patient ID"));

			pidTextBox = new TextBox();
			pidTextBox.setWidth("400px");
			pidTextBox.setText(getCommonPatientId());
			pidTextBox.addChangeHandler(new PidChangeHandler(this));
			commonParamGrid.setWidget(commonGridRow++, contentsColumn, pidTextBox);
		}

		SiteSpec commonSiteSpec = null;
		if (samlEnabled) {
			commonParamGrid.setWidget(commonGridRow, titleColumn, new HTML("SAML"));

			commonSiteSpec = getCommonSiteSpec();

			samlListBox = new ListBox();
			samlListBox.addItem("SAML OFF", "0");
			samlListBox.addItem("NHIN SAML", "1");
			samlListBox.setVisibleItemCount(1);
			samlListBox.addChangeHandler(new SamlSelector(this));
			if (commonSiteSpec != null)
				samlListBox.setSelectedIndex((commonSiteSpec.isSaml) ? 1 : 0);
			commonParamGrid.setWidget(commonGridRow++, contentsColumn, samlListBox);
		}


		if (tlsEnabled) {
			doTls = new CheckBox("");
			if (commonSiteSpec != null)
				doTls.setValue(getCommonSiteSpec().isTls());
			doTls.addClickHandler(new TlsSelector(this));
			commonParamGrid.setWidget(commonGridRow, titleColumn, new HTML("TLS"));
			commonParamGrid.setWidget(commonGridRow++, contentsColumn, doTls);
		}


		if (asyncEnabled) {
			CheckBox doAsync = new CheckBox("Async?");
			doAsync.setValue(doASYNC);
			doAsync.addClickHandler(new AsyncSelector(this));
			mainGrid.setWidget(row, 1, doAsync);
			row++;
		}

		if (selectByActor != null) {  // this is only used in Mesa test related panels
			HTML label = new HTML();
			label.setHTML("Site");
			mainGrid.setWidget(row, 0, label);
			byActorButtons = siteLoader.addSitesForActor(selectByActor, row);
			row++;
		} else if (transactionTypes != null){    // most queries and retrieves use this
			commonParamGrid.setWidget(commonGridRow, titleColumn, new HTML("Site"));
			FlexTable siteGrid = new FlexTable();
			commonParamGrid.setWidget(commonGridRow++, contentsColumn, siteGrid);
			int siteGridRow = 0;
			for (TransactionType tt : transactionTypes) {
				ActorType at = ActorType.getActorType(tt);  
				siteGrid.setWidget(siteGridRow, 0, new HTML(at.getName()));
				siteGrid.setWidget(siteGridRow++, 1, getSiteTableWidgetforTransactions(tt));
			}
		}

		HorizontalPanel runnerButtons = new HorizontalPanel();
		commonParamGrid.setWidget(commonGridRow++, 1, runnerButtons);
		if (runEnabled) {
			setGoButton(new Button(runButtonText));
			runnerButtons.add(getGoButton());
//			mainGrid.setWidget(row++, 1, getGoButton());
		}

		try {
			getGoButton().addClickHandler(runner);
		} catch (Exception e) {}

		if (enableInspectResults) {
			setInspectButton(new Button("Inspect Results"));
			getInspectButton().setEnabled(false);
			runnerButtons.add(getInspectButton());
		}

		if (getInspectButton() != null)
			getInspectButton().addClickHandler(new InspectorLauncher(me));

		resultsShortDescription.setHTML("");
		runnerButtons.add(resultsShortDescription);
	}

	public void setRunButtonText(String label) {
		runButtonText = label;
	}


	// since to has come over from server and tt was generated here, they
	// don't align hashvalues.  Search must be done the old fashion way
	List<Site> findSites(TransactionType tt, boolean tls) {

		// aka testSession

		return siteLoader.findSites(tt, tls);
	}

	Widget getSiteTableWidgetforTransactions(TransactionType tt) {
		if (transactionSelectionManager == null)
			transactionSelectionManager = new TransactionSelectionManager(couplings, this);
		List<Site> sites = getSiteList(tt); 
		transactionSelectionManager.addTransactionType(tt, sites);

		int cols = 5;
		int row=0;
		int col=0;
		Grid grid = new Grid( sites.size()/cols + 1 , cols);
		for (RadioButton rb : transactionSelectionManager.getRadioButtons(tt)) {
			grid.setWidget(row, col, rb);
			col++;
			if (col >= cols) {
				col = 0;
				row++;
			}
		}
//		mainGrid.setWidget(majorRow, startingCol, grid);
		return grid;
	}

	List<Site> getSiteList(TransactionType tt) {
		List<Site> sites = siteLoader.findSites(tt, isTLS());

		List<String> siteNames = new ArrayList<String>();
		for (Site site : sites) 
			siteNames.add(site.getName());
		siteNames = new StringSort().sort(siteNames);

		List<Site> orderedSites = new ArrayList<Site>();
		for (String siteName : siteNames) {
			for (Site site : sites) {
				if (siteName.equals(site.getName())) {
					orderedSites.add(site);
					break;
				}
			}
		}
		return sites;
	}

	protected SiteSpec getSiteSelection() { return queryBoilerplate.getSiteSelection(); }

	protected boolean verifyPidProvided() {
		if (pidTextBox.getValue() == null || pidTextBox.getValue().equals("")) {
			new PopupMessage("You must enter a Patient ID first");
			return false;
		}
		return true;
	}

	protected boolean verifySiteProvided() {
		SiteSpec siteSpec = getSiteSelection();
		if (siteSpec == null) {
			new PopupMessage("You must select a site first");
			return false;
		}
		return true;
	}

	protected void rigForRunning() {
		resultPanel.clear();
		// Where the bottom-of-screen listing from server goes
		addStatusBox();
		getGoButton().setEnabled(false);
		getInspectButton().setEnabled(false);
	}
}
