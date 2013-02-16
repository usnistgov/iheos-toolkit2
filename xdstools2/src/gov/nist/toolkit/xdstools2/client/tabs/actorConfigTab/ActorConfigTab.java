package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionCollection;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.StringSort;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ActorConfigTab extends GenericQueryTab {
	ListBox siteSelector;
	FlexTable actorEditGrid;
	int actorEditRow = -1;
	HTML signInStatus;
	Hyperlink signIn = new Hyperlink();
	boolean enableGazelleReload = false;
	Button reloadFromGazelleButton;
	CheckBox showSims = new CheckBox();
	
	Site currentEditSite = null;


	public ActorConfigTab() {
		super(new NullSiteActorManager());
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();

		container.addTab(topPanel, "ActorConfig", select);
		addCloseButton(container,topPanel, null);
		
		loadGazelleFeedAvailableStatus();

		HTML title = new HTML();
		title.setHTML("<h2>Configure Sites</h2>");
		topPanel.add(title);

		Anchor reload = new Anchor();
		reload.setText("[reload]");
		reload.addClickHandler(new ReloadClickHandler(this));
		menuPanel.add(reload);

		mainGrid = new FlexTable();
		int row = 0;

		topPanel.add(mainGrid);

		reloadExternalSites();

		VerticalPanel sitesPanel = new VerticalPanel();

		siteSelector = new ListBox();
		siteSelector.setVisibleItemCount(15);
		sitesPanel.add(siteSelector);

		HorizontalPanel actionButtons = new HorizontalPanel();

		Button newSiteButton = new Button("+");
		newSiteButton.addClickHandler(new CreateNewSite(this));
		actionButtons.add(newSiteButton);

		Button rmSiteButton = new Button("-");
		rmSiteButton.addClickHandler(new DeleteSite(this));
		actionButtons.add(rmSiteButton);

		sitesPanel.add(actionButtons);

		HorizontalPanel signOutPanel = new HorizontalPanel();

		signInStatus = new HTML();
		updateSignInStatus();
		signOutPanel.add(signInStatus);

		signIn.setText("[Sign Out]");
		signOutPanel.add(signIn);

		signIn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				PasswordManagement.isSignedIn = false;
				updateSignInStatus();
			}

		});


		sitesPanel.add(signOutPanel);
		
		showSims.setText("Show Sims");
		showSims.setValue(false);
		
		showSims.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				new ReloadClickHandler(ActorConfigTab.this).onClick(null);
			}
			
		});
		
		sitesPanel.add(showSims);
		
		mainGrid.setWidget(row, 0, sitesPanel);

		actorEditRow = row;
		newActorEditGrid();
		
	}

	void loadGazelleFeedAvailableStatus() { 

		final AsyncCallback<Boolean> gazelleConfigEnabledCallback = new AsyncCallback<Boolean> () {

			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());
			}

			public void onSuccess(Boolean enabled) {
				enableGazelleReload = enabled;
				if (reloadFromGazelleButton != null)
					reloadFromGazelleButton.setEnabled(enabled);
			}

		};

		toolkitService.isGazelleConfigFeedEnabled(gazelleConfigEnabledCallback);

	}

	void updateSignInStatus() {
		if (PasswordManagement.isSignedIn) {
			signInStatus.setText("Signed In   ");
		}
		else {
			signInStatus.setText("Signed Out   ");
		}		
		signIn.setVisible(PasswordManagement.isSignedIn);
	}

	void newActorEditGrid() {
		actorEditGrid = new FlexTable();
		mainGrid.setWidget(actorEditRow, 1, actorEditGrid);
	}

	List<String> currentSiteNames = null;
	
	void reloadExternalSites() {

		final AsyncCallback<List<String>> loadSiteNamesCallback = new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());
			}

			public void onSuccess(List<String> result) {
				loadSiteNames(result);
			}
		};
		toolkitService.reloadExternalSites(loadSiteNamesCallback);
	}


	String newSiteName = "NewSite";


	void displaySite(Site site) {
		site.changed = false;
		currentEditSite = site;
		int row = 0;
		String boxwidth = "600px";
		boolean TLS = true;

		HTML nameLabel = new HTML(HtmlMarkup.bold("Site Name"));
		actorEditGrid.setWidget(row, 0, nameLabel);

		TextBox nameBox = new TextBox();
		nameBox.setWidth("200px");
		nameBox.setText(trim(currentEditSite.getName()));
		nameBox.addChangeHandler(new NameChangedHandler(this, currentEditSite, nameBox));
		actorEditGrid.setWidget(row, 1, nameBox);
		row++;

		actorEditGrid.setWidget(row, 1, new HTML(HtmlMarkup.bold(getTlsLabel(booleanValues().get(0)) + " Endpoints")));
		actorEditGrid.setWidget(row, 2, new HTML(HtmlMarkup.bold(getTlsLabel(booleanValues().get(1)) + " Endpoints")));
		row++;

		for (ActorType actorType : TransactionCollection.getActorTypes()) {
			
			// These get configed in other ways
			if (!actorType.showInConfig())
				continue;
			
			String actorTypeName = actorType.getName();
			HTML actorTypeLabel = new HTML(HtmlMarkup.bold(actorTypeName));
			actorEditGrid.setWidget(row, 1, actorTypeLabel);
			row++;

			/**
			 * Prefix entries that are needed before standard entries
			 */
			if (ActorType.RESPONDING_GATEWAY.equals(actorType)) {
				actorEditGrid.setWidget(row, 0, new Label("homeCommunityId"));

				TextBox homeBox = new TextBox();
				homeBox.setWidth(boxwidth);
				homeBox.setText(trim(currentEditSite.home));
				homeBox.addChangeHandler(new HomeChangedHandler(this, currentEditSite, homeBox));
				actorEditGrid.setWidget(row, 1, homeBox);

				row++;
			}

			if (ActorType.REPOSITORY.equals(actorType)) {
				Label repuidLabel = new Label("repostoryUniqueId");
				actorEditGrid.setWidget(row, 0, repuidLabel);

				TextBox repuidBox = new TextBox();
				repuidBox.setWidth(boxwidth);

				String repuid = "";

				boolean isAsync = false;

				TransactionBean repBean = currentEditSite.getRepositoryBean(false);
				TransactionBean secureRepBean = currentEditSite.getRepositoryBean(true);
				TransactionBean transBean = currentEditSite.transactions().find(TransactionType.RETRIEVE, false, isAsync);
				TransactionBean secureTransBean = currentEditSite.transactions().find(TransactionType.RETRIEVE, true, isAsync);
				
				if (repBean == null) {
					repBean = new TransactionBean("", RepositoryType.REPOSITORY, "", !TLS, isAsync);
					currentEditSite.addRepository(repBean);
				} else {
					repuid = trim(repBean.getName());
				}

				if (secureRepBean == null) {
					secureRepBean = new TransactionBean("", RepositoryType.REPOSITORY, "", TLS, isAsync);
					currentEditSite.addRepository(secureRepBean);
				} else if (repuid.equals("")) {
					repuid = trim(secureRepBean.getName());
				}

				if (transBean == null) {
					transBean = new TransactionBean(TransactionType.RETRIEVE, RepositoryType.REPOSITORY, "", !TLS, isAsync);
					currentEditSite.addTransaction(transBean);
				}
				
				if (secureTransBean == null) {
					secureTransBean = new TransactionBean(TransactionType.RETRIEVE, RepositoryType.REPOSITORY, "", TLS, isAsync);
					currentEditSite.addTransaction(secureTransBean);
				}

				repuidBox.setText(repuid);
				repuidBox.addChangeHandler(new RepuidChangedHandler(this, repBean, repuidBox));
				repuidBox.addChangeHandler(new RepuidChangedHandler(this, secureRepBean, repuidBox));
				actorEditGrid.setWidget(row, 1, repuidBox);

				row++;

				actorEditGrid.setWidget(row, 0, new Label("Retrieve"));

				int TLS_COLUMN = 1;
				int NONTLS_COLUMN = 2;

				String retEndpoint = repBean.endpoint;
				TextBox retEndpointBox = new TextBox();
				retEndpointBox.setWidth(boxwidth);
				retEndpointBox.setText(trim(retEndpoint));
				retEndpointBox.addValueChangeHandler(new EndpointChangedHandler(this, repBean, transBean, retEndpointBox));
				actorEditGrid.setWidget(row, NONTLS_COLUMN, retEndpointBox);

				String secRetEndpoint = secureRepBean.endpoint;
				TextBox secRetEndpointBox = new TextBox();
				secRetEndpointBox.setWidth(boxwidth);
				secRetEndpointBox.setText(trim(secRetEndpoint));
				secRetEndpointBox.addValueChangeHandler(new EndpointChangedHandler(this, secureRepBean, secureTransBean, secRetEndpointBox));
				actorEditGrid.setWidget(row, TLS_COLUMN, secRetEndpointBox);
				
//				for (Boolean isSecure : booleanValues()) {
//					TransactionBean transbean = site.transactions().find(TransactionType.RETRIEVE, isSecure, isAsync);
//					if (transbean == null) {
//						transbean = new TransactionBean(TransactionType.RETRIEVE, RepositoryType.REPOSITORY, "", isSecure, isAsync);
//						currentEditSite.addTransaction(transbean);
//					}
//					TextBox endpointBox = new TextBox();
//					endpointBox.setWidth(boxwidth);
//					endpointBox.setText(trim(transbean.endpoint));
//					endpointBox.addValueChangeHandler(new EndpointChangedHandler(this, transbean, endpointBox));
//					actorEditGrid.setWidget(row, (isSecure) ? TLS_COLUMN : NONTLS_COLUMN, endpointBox);
//				}

				row++;

			}
			
			if (ActorType.REGISTRY.equals(actorType)) {
				HorizontalPanel hpanel = new HorizontalPanel();
				Label pifLabel = new Label("Patient Identity Feed");
				actorEditGrid.setWidget(row, 0, pifLabel);
				
				hpanel.add(new Label("host"));
				TextBox hostbox = new TextBox();
				hostbox.setWidth("300px");
				hostbox.setText(site.pifHost);
				hpanel.add(hostbox);
				
				hpanel.add(new Label("port"));
				TextBox portbox = new TextBox();
				portbox.setWidth("100px");
				portbox.setText(site.pifPort);
				hpanel.add(portbox);
				
				actorEditGrid.setWidget(row, 1, hpanel);
				
				row++;
			}

			for(TransactionType transType : actorType.getTransactions()) {
				if (transType == TransactionType.RETRIEVE)
					continue;   // Handled above
				HTML transNameLabel = new HTML(transType.getName());
				actorEditGrid.setWidget(row, 0, transNameLabel);

				boolean isAsync = false;
				for (Boolean isSecure : booleanValues()) {
					TransactionBean transbean = site.transactions().find(transType, isSecure, isAsync);
					if (transbean == null) {
						transbean = new TransactionBean(transType, RepositoryType.REPOSITORY, "", isSecure, isAsync);
						site.addTransaction(transbean);
					}
					TextBox endpointBox = new TextBox();
					endpointBox.setWidth(boxwidth);
					endpointBox.setText(trim(transbean.endpoint));
					endpointBox.addValueChangeHandler(new EndpointChangedHandler(this, transbean, endpointBox));
					actorEditGrid.setWidget(row, (isSecure) ? 1 : 2, endpointBox);

				}
				row++;
			}


		}

		Button saveButton = new Button("Save Changes");
		saveButton.addClickHandler(new SaveButtonClickHandler(this));
		actorEditGrid.setWidget(row, 0, saveButton);

		Button forgetButton = new Button("Forget Changes");
		forgetButton.addClickHandler(new ForgetButtonClickHandler(this));
		actorEditGrid.setWidget(row, 1, forgetButton);
		
		reloadFromGazelleButton = new Button("Reload from Gazelle");
		reloadFromGazelleButton.addClickHandler(new ReloadSystemFromGazelleClickHandler(this));
		actorEditGrid.setWidget(row, 2, reloadFromGazelleButton);
		reloadFromGazelleButton.setEnabled(enableGazelleReload);

	}

	String trim(String s) {
		if (s == null)
			return "";
		return s.trim();
	}
		
	// Boolean data type ignored 
	AsyncCallback<Boolean> saveSignedInCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
		}

		public void onSuccess(Boolean ignored) {
			updateSignInStatus();
			currentEditSite.cleanup();
			newActorEditGrid();
			saveSite(currentEditSite);
		}

	};

	void saveSite(Site site) {
		final AsyncCallback<String> saveSiteCallback = new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("Error saving site configuration: " + caught.getMessage());
			}

			public void onSuccess(String ignore) {
				currentEditSite.changed = false;
				toolkitService.getSiteNames(true, showSims.getValue(), new AsyncCallback<List<String>>() {
					public void onFailure(Throwable caught) {
						new PopupMessage(caught.getMessage());
					}

					public void onSuccess(List<String> result) {
						loadSiteNames(result);
					}
				});
			}

		};
		toolkitService.saveSite(currentEditSite, saveSiteCallback);
	}
	
	void loadExternalSites() {

		final AsyncCallback<List<String>> loadSiteNamesCallback = new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());
			}

			public void onSuccess(List<String> result) {
				loadSiteNames(result);
			}
		};
		toolkitService.getSiteNames(true, showSims.getValue(), loadSiteNamesCallback);
	}

	void loadSiteNames(List<String> result) {
		newActorEditGrid();

		siteSelector.clear();
		for (String site : new StringSort().sort(result)) {
			if (site.equals("allRepositories"))
				continue;
			siteSelector.addItem(site);
		}
		siteSelector.addClickHandler(new SiteChoose(ActorConfigTab.this));

		currentSiteNames = result;
	}

	String getTlsLabel(boolean useTls) {
		if (useTls)
			return "TLS";
		return "non-TLS";
	}

	List<Boolean> booleanValues() {
		List<Boolean> vals = new ArrayList<Boolean>();
		vals.add(new Boolean(true));
		vals.add(new Boolean(false));
		return vals;
	}

	public String getWindowShortName() {
		return "actorconfig";
	}

}
