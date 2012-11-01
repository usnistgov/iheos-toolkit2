package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.StringSort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QueryBoilerplate {
	/**
	 * 
	 */
	private final GenericQueryTab genericQueryTab;
	int row_initial;
	int row;
	boolean tlsEnabled = true;
	boolean samlEnabled = true;
	boolean enableInspectResults = true;
	boolean runEnabled = true;
	ClickHandler runner;
	Anchor reload;
	List<TransactionType> transactionTypes;
	CoupledTransactions couplings;
	ActorType selectByActor = null;
	public TransactionSelectionManager transactionSelectionManager = null;
	
	public void enableRun(boolean enable) {
		runEnabled = enable;
	}

	QueryBoilerplate(GenericQueryTab genericQueryTab, ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings, ActorType selectByActor) {
		this.genericQueryTab = genericQueryTab;
		this.selectByActor = selectByActor;
		this.row_initial = genericQueryTab.mainGrid.getRowCount();
		this.runner = runner;
		this.transactionTypes = transactionTypes;
		this.couplings = couplings;

		genericQueryTab.resultPanel = new VerticalPanel();
		genericQueryTab.topPanel.add(genericQueryTab.resultPanel);


		reload = new Anchor();
		reload.setTitle("Reload actors configuration");
		reload.setText("[reload]");
		genericQueryTab.me.addToMenu(reload);

		reload.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				//					redisplay();
				reloadTransactionOfferings();
			}

		});

		if (GenericQueryTab.transactionOfferings == null) {
			reloadTransactionOfferings();
		} else {
			redisplay();
		}
	}

	QueryBoilerplate(GenericQueryTab genericQueryTab2, ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings) {
		this(genericQueryTab2, runner, transactionTypes, couplings, null);

	}

	public void enableTls(boolean enable) {
		tlsEnabled = enable;
	}

	public void enableSaml(boolean enable) {
		samlEnabled = enable;
	}
	
	public void enableInspectResults(boolean enable) {
		enableInspectResults = enable;
	}

	void reloadTransactionOfferings() {
		try {
			genericQueryTab.toolkitService.getTransactionOfferings(new AsyncCallback<TransactionOfferings> () {

				public void onFailure(Throwable caught) {
					genericQueryTab.resultPanel.clear();
					genericQueryTab.resultPanel.add(genericQueryTab.addHTML("<font color=\"#FF0000\">" + "Error: " + caught.getMessage() + "</font>"));
				}

				public void onSuccess(TransactionOfferings to) {
					GenericQueryTab.transactionOfferings = to;
					redisplay();
				}

			});
		} catch (Exception e) {
			genericQueryTab.resultPanel.clear();
			genericQueryTab.resultPanel.add(genericQueryTab.addHTML("<font color=\"#FF0000\">" + "Error: " + e.getMessage() + "</font>"));
		}
	}

	void remove() {
		genericQueryTab.topPanel.remove(genericQueryTab.resultPanel);
		genericQueryTab.menuPanel.remove(reload);
	}

	// clean out mainGrid so the actors can be re-added
	void initMainGrid() {
		while (genericQueryTab.mainGrid.getRowCount() > row_initial)
			genericQueryTab.mainGrid.removeRow(genericQueryTab.mainGrid.getRowCount() - 1);
		row = row_initial;
	}

	boolean isDisplayGW() {
		for (TransactionType tt : transactionTypes) {
			if (ATFactory.isGatewayTransaction(tt))
				return true;
		}
		return false;
	}

	// since to has come over from server and tt was generated here, they
	// don't align hashvalues.  Search must be done the old fashion way
	List<Site> findSites(TransactionType tt, boolean tls) {
		Map<TransactionType, List<Site>> map;

		if (tls) {
			map = GenericQueryTab.transactionOfferings.tmap;
		} else {
			map = GenericQueryTab.transactionOfferings.map;
		}

		for (TransactionType t : map.keySet()) {
			if (t.getName().equals(tt.getName()))
				return map.get(t);
		}
		return new ArrayList<Site>();
	}

	public List<RadioButton> addSitesForActor(ActorType actorType, int majorRow) {

		Set<Site> sites = new HashSet<Site>();

		List<String> siteNames = new ArrayList<String>();
		for (Site site : sites) 
			siteNames.add(site.getName());
		siteNames = new StringSort().sort(siteNames);

		for (TransactionType tt : actorType.getTransactions()) {
			sites.addAll(findSites(tt, true  /* tls */));
			sites.addAll(findSites(tt, false /* tls */));
		}

		int cols = 5;
		int row=0;
		int col=0;
		Grid grid = new Grid( sites.size()/cols + 1 , cols);
		List<RadioButton> buttons = new ArrayList<RadioButton>();

		SiteSpec commonSiteSpec = genericQueryTab.getCommonSiteSpec();

		for (Site site : sites) {
			String siteName = site.getName();
			RadioButton rb = new RadioButton(actorType.getName(), siteName);

			if (
					commonSiteSpec.getName().equals(actorType.getName())  
					//	&& commonSiteSpec.getActorType() == actorType
					) 
				rb.setValue(true);
			if (
					commonSiteSpec.getName().equals(siteName) 
					//	&& commonSiteSpec.getActorType() == actorType
					)
				rb.setValue(true);

			buttons.add(rb);
			grid.setWidget(row, col, rb);
			col++;
			if (col >= cols) {
				col = 0;
				row++;
			}

		}
		genericQueryTab.mainGrid.setWidget(majorRow, 1, grid);

		return buttons;
	}

	public SiteSpec getSiteSelection() {
		if (selectByActor != null) {    // Used in Mesa test tab
			for (RadioButton b : genericQueryTab.byActorButtons) {
				if (b.getValue()) {
					genericQueryTab.setCommonSiteSpec(new SiteSpec(b.getText(), selectByActor, genericQueryTab.getCommonSiteSpec()));
					return genericQueryTab.getCommonSiteSpec();
				}
			}
		} else {   // Select by transaction (used in GetDocuments tab)
			SiteSpec siteSpec = transactionSelectionManager.generateSiteSpec();
			genericQueryTab.setCommonSiteSpec(siteSpec);
			return siteSpec;
			//				for (TransactionType tt : genericQueryTab.perTransTypeRadioButtons.keySet()) {
			//					for (RadioButton rb : genericQueryTab.perTransTypeRadioButtons.get(tt)) {
			//						if (rb.getValue()) {
			//							genericQueryTab.getCommonSiteSpec().setName(rb.getText());
			//							genericQueryTab.getCommonSiteSpec().setActorType(ActorType.getActorType(tt));
			//							return genericQueryTab.getCommonSiteSpec();
			//						}
			//					}
			//				}
		}

		return null;
	}

	public String getPatientId() { return genericQueryTab.getCommonPatientId(); }

	void redisplay() {

		if (genericQueryTab.resultPanel != null)
			genericQueryTab.resultPanel.clear();

		//			genericQueryTab.perTransTypeRadioButtons = new HashMap<TransactionType, List<RadioButton>>();

		initMainGrid();

		if (genericQueryTab.hasPatientIdParam) {
			HTML pidLabel = new HTML();
			pidLabel.setText("Patient ID");
			genericQueryTab.mainGrid.setWidget(row,0, pidLabel);
			genericQueryTab.pidTextBox = new TextBox();
			genericQueryTab.pidTextBox.setWidth("400px");
			genericQueryTab.pidTextBox.setText(genericQueryTab.getCommonPatientId());
			genericQueryTab.pidTextBox.addChangeHandler(new PidChangeHandler(genericQueryTab));
			genericQueryTab.mainGrid.setWidget(row, 1, genericQueryTab.pidTextBox);
			row++;
		}

		SiteSpec commonSiteSpec = null;
		if (samlEnabled) {
			HTML samlListLabel = new HTML();
			samlListLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			samlListLabel.setText("SAML");
			genericQueryTab.mainGrid.setWidget(row, 0, samlListLabel);

			commonSiteSpec = genericQueryTab.getCommonSiteSpec();

			genericQueryTab.samlListBox = new ListBox();
			genericQueryTab.samlListBox.addItem("SAML OFF", "0");
			genericQueryTab.samlListBox.addItem("NHIN SAML", "1");
			genericQueryTab.samlListBox.setVisibleItemCount(1);
			genericQueryTab.samlListBox.addChangeHandler(new SamlSelector(genericQueryTab));
			genericQueryTab.mainGrid.setWidget(row, 1, genericQueryTab.samlListBox);
			if (commonSiteSpec != null)
				genericQueryTab.samlListBox.setSelectedIndex((commonSiteSpec.isSaml) ? 1 : 0);
			row++;
		}


		if (tlsEnabled) {
			genericQueryTab.doTls = new CheckBox("TLS?");
			if (commonSiteSpec != null)
				genericQueryTab.doTls.setValue(genericQueryTab.getCommonSiteSpec().isTls());
			genericQueryTab.doTls.addClickHandler(new TlsSelector(genericQueryTab));
			genericQueryTab.mainGrid.setWidget(row, 1, genericQueryTab.doTls);
			row++;
		}


		if (genericQueryTab.asyncEnabled) {
			CheckBox doAsync = new CheckBox("Async?");
			doAsync.setValue(genericQueryTab.doASYNC);
			doAsync.addClickHandler(new AsyncSelector(genericQueryTab));
			genericQueryTab.mainGrid.setWidget(row, 1, doAsync);
		}
		row++;


		if (selectByActor != null) {  // this is only used in Mesa test related panels
			HTML label = new HTML();
			label.setHTML("Site");
			genericQueryTab.mainGrid.setWidget(row, 0, label);
			genericQueryTab.byActorButtons = addSitesForActor(selectByActor, row);
			row++;
		} else if (transactionTypes != null){    // most queries and retrieves use this
			for (TransactionType tt : transactionTypes) {
				ActorType at = ActorType.getActorType(tt);  
				HTML label = new HTML();
				label.setHTML(at.getName());    // actor type (Registry or IG etc)
				genericQueryTab.mainGrid.setWidget(row, 0, label);

				addSitesForTransaction(tt, row);
				row++;
			}
		}

		genericQueryTab.resultPanel = new VerticalPanel();
		genericQueryTab.topPanel.add(genericQueryTab.resultPanel);

		if (runEnabled) {
			genericQueryTab.setGoButton(new Button("Run"));
			genericQueryTab.mainGrid.setWidget(row++, 1, genericQueryTab.getGoButton());
		}

		try {
			genericQueryTab.getGoButton().addClickHandler(runner);
		} catch (Exception e) {}

		if (enableInspectResults) {
		genericQueryTab.setInspectButton(new Button("Inspect Results"));
		genericQueryTab.getInspectButton().setEnabled(false);
		genericQueryTab.mainGrid.setWidget(row++, 1, genericQueryTab.getInspectButton());
		}

		if (genericQueryTab.getInspectButton() != null)
			genericQueryTab.getInspectButton().addClickHandler(new InspectorLauncher(genericQueryTab.me));
	}

	void addSitesForTransaction(TransactionType tt, int majorRow) {
		if (transactionSelectionManager == null)
			transactionSelectionManager = new TransactionSelectionManager(couplings, genericQueryTab);
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

			//				SiteSpec commonSiteSpec = genericQueryTab.getCommonSiteSpec();
			//				if (
			//						commonSiteSpec.getName().equals(siteName) &&
			//						commonSiteSpec.getActorType().hasTransaction(tt))
			//					rb.setValue(true);
		}
		genericQueryTab.mainGrid.setWidget(majorRow, 1, grid);
	}

	List<Site> getSiteList(TransactionType tt) {
		List<Site> sites = findSites(tt, genericQueryTab.isTLS());

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


}