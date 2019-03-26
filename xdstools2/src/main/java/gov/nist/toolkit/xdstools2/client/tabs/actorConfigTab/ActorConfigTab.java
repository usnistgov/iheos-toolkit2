package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.StringSort;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.sitemanagement.client.TransactionCollection;
import gov.nist.toolkit.xdstools2.client.NotifyOnDelete;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.GetSiteNamesCommand;
import gov.nist.toolkit.xdstools2.client.command.command.IsGazelleConfigFeedEnabledCommand;
import gov.nist.toolkit.xdstools2.client.command.command.SaveSiteCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.InformationLink;
import gov.nist.toolkit.xdstools2.client.widgets.AccessControlledMenuItem;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSiteNamesRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.SaveSiteRequest;

import java.util.ArrayList;
import java.util.List;

public class ActorConfigTab extends GenericQueryTab implements NotifyOnDelete {
    public static final String TAB_NAME = "SystemConfig";
    ListBox siteSelector;
	private FlexTable actorEditGrid;
	private int actorEditRow = -1;
//	private HTML signInStatus;
//	private Hyperlink signIn = new Hyperlink();
	private boolean enableGazelleReload = false;
	private Button reloadFromGazelleButton;
	private CheckBox showSims = new CheckBox();
	private AccessControlledMenuItem<Button> acmiSaveButton;
	private AccessControlledMenuItem<Button> acmiPromoteSite;
	private AccessControlledMenuItem<Button> acmiDeleteSite;
	private AccessControlledMenuItem<Button> acmiCreateSite;
	Site currentEditSite = null;


	public ActorConfigTab() {
		super(new NullSiteActorManager(), 0.0, 20.0);
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

	private HandlerRegistration testSessionChangedHandler = null;

	@Override
	public void onDelete() {
		// Remove existing test session change handler
		if (testSessionChangedHandler != null) {
			testSessionChangedHandler.removeHandler();
			testSessionChangedHandler = null;
		}
		// Release the acmi
		if (acmiSaveButton !=null) {
			if (PasswordManagement.adminMenuItemList.contains(acmiSaveButton))
				PasswordManagement.adminMenuItemList.remove(acmiSaveButton);
		}
		if (acmiPromoteSite!=null) {
			if (PasswordManagement.adminMenuItemList.contains(acmiPromoteSite))
				PasswordManagement.adminMenuItemList.remove(acmiPromoteSite);
		}
		if (acmiDeleteSite!=null) {
			if (PasswordManagement.adminMenuItemList.contains(acmiDeleteSite))
				PasswordManagement.adminMenuItemList.remove(acmiDeleteSite);
		}
		if (acmiCreateSite!=null) {
			if (PasswordManagement.adminMenuItemList.contains(acmiCreateSite))
				PasswordManagement.adminMenuItemList.remove(acmiCreateSite);
		}
	}


	@Override
	public void onTabLoad(boolean select, String eventName) {
//		registerTab(select, TAB_NAME);
		registerDeletableTab(select, eventName, this);

		// Reload if the test session changes
		testSessionChangedHandler = ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
					loadExternalSites();
					((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
				}
			}
		});

		loadGazelleFeedAvailableStatus();

		tabTopPanel.add(new InformationLink("System configuration help", "Managing System Configurations at Connectathon"));

		HTML title = new HTML();
		title.setHTML("<h2>Configure Systems</h2>");
		tabTopPanel.add(title);




		Anchor reload = new Anchor();
		reload.setText("[reload]");
		reload.addClickHandler(new ReloadClickHandler(this));
		menuPanel.add(reload);

		mainGrid = new FlexTable();
		int row = 0;

		tabTopPanel.add(mainGrid);

		reloadExternalSites();

		FlowPanel sitesPanel = new FlowPanel();

		siteSelector = new ListBox();
		siteSelector.setVisibleItemCount(15);
		sitesPanel.add(siteSelector);

		HorizontalPanel actionButtons = new HorizontalPanel();

		Button newSiteButton = new Button("+");
		acmiCreateSite = new AccessControlledMenuItem<Button>(newSiteButton, new CreateNewSite(this), AccessControlledMenuItem.IndicatorType.DISABLE_FEATURE) {
			@Override
			public boolean isAccessible() {
				return Xdstools2.getInstance().isSystemSaveEnabled();
			}
		};
		actionButtons.add(acmiCreateSite);

		Button rmSiteButton = new Button("-");
		acmiDeleteSite = new AccessControlledMenuItem<Button>(rmSiteButton, new DeleteSite(this), AccessControlledMenuItem.IndicatorType.DISABLE_FEATURE) {
			@Override
			public boolean isAccessible() {
				return Xdstools2.getInstance().isSystemSaveEnabled();
			}
		};
		actionButtons.add(acmiDeleteSite);

		sitesPanel.add(actionButtons);

//		HorizontalPanel signOutPanel = new HorizontalPanel();
//
//		signInStatus = new HTML();
//		updateSignInStatus();
//		signOutPanel.add(signInStatus);
//
//		signIn.setText("[Sign Out]");
//		signOutPanel.add(signIn);
//
//		signIn.addClickHandler(new ClickHandler() {
//
//			public void onClick(ClickEvent event) {
//				PasswordManagement.isSignedIn = false;
//				updateSignInStatus();
//			}
//
//		});
//
//
//		sitesPanel.add(signOutPanel);

		showSims.setText("Show Sims");
		showSims.setValue(false);

		showSims.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				new ReloadClickHandler(ActorConfigTab.this).onClick(null);
			}

		});

		sitesPanel.add(showSims);

//		mainGrid.setWidget(row, 0, sitesPanel);
		addWest(sitesPanel);

		actorEditRow = row;
		newActorEditGrid();

		sitesPanel.add(new HTML("<br />"));
		Button saveButton = new Button("Save Changes");
		acmiSaveButton = new AccessControlledMenuItem<Button>(saveButton, new SaveButtonClickHandler(this), AccessControlledMenuItem.IndicatorType.DISABLE_FEATURE) {
			@Override
			public boolean isAccessible() {
				return Xdstools2.getInstance().isSystemSaveEnabled();
			}
		};
//		saveButton.addClickHandler();
//		sitesPanel.add(saveButton);
        sitesPanel.add(acmiSaveButton);

		sitesPanel.add(new HTML("<br />"));
		Button forgetButton = new Button("Forget Changes");
		forgetButton.addClickHandler(new ForgetButtonClickHandler(this));
		sitesPanel.add(forgetButton);

		sitesPanel.add(new HTML("<br />"));
		Button reloadSitesBtn=new Button("Reload from server");
		reloadSitesBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				reloadExternalSites();
				((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
			}
		});
		sitesPanel.add(reloadSitesBtn);

		sitesPanel.add(new HTML("<br />"));
		Button promoteButton = new Button("Promote");
		acmiPromoteSite = new AccessControlledMenuItem<Button>(promoteButton, new PromoteClickHandler(this), AccessControlledMenuItem.IndicatorType.DISABLE_FEATURE)  {
			@Override
			public boolean isAccessible() {
				return  Xdstools2.getInstance().isSystemSaveEnabled();
			}
		};
		sitesPanel.add(acmiPromoteSite);

		sitesPanel.add(new HTML("<br />"));
		reloadFromGazelleButton = new Button("Reload from Gazelle");
		reloadFromGazelleButton.addClickHandler(new ReloadSystemFromGazelleClickHandler(this));
		sitesPanel.add(reloadFromGazelleButton);
		reloadFromGazelleButton.setEnabled(enableGazelleReload);

	}

	private void loadGazelleFeedAvailableStatus() {
		new IsGazelleConfigFeedEnabledCommand(){
			@Override
			public void onComplete(Boolean result) {
				enableGazelleReload = result;
				if (reloadFromGazelleButton != null)
					reloadFromGazelleButton.setEnabled(result);
			}
		}.run(getCommandContext());
	}

	void updateSignInStatus() {
//		if (PasswordManagement.isSignedIn) {
//			signInStatus.setText("Signed In   ");
//		}
//		else {
//			signInStatus.setText("Signed Out   ");
//		}
//		signIn.setVisible(PasswordManagement.isSignedIn);
	}

	void newActorEditGrid() {
		actorEditGrid = new FlexTable();
		mainGrid.setWidget(actorEditRow, 1, actorEditGrid);
	}

	List<String> currentSiteNames = null;

	private void reloadExternalSites() {
		new GetSiteNamesCommand(){

			@Override
			public void onComplete(List<String> result) {
				int allRepos = -1;
				int i = 0;
				for (String s : result) {
					if (s.endsWith("allRepositories")) {
						allRepos = i;
						break;
					}
					i++;
				}
				if (i != -1)
					result.remove(i);
				loadSiteNames(result);
			}
		}.run(new GetSiteNamesRequest(getCommandContext(),true,showSims.getValue()).withQualified(PasswordManagement.isSignedIn));
//		new ReloadExternalSitesCommand(){
//            @Override
//            public void onComplete(List<String> result) {
//                loadSiteNames(result);
//            }
//        }.run(getCommandContext());
	}

	String newSiteName = "NewSite";

	void displaySite(Site site) {
		site.changed = false;
		currentEditSite = site;
		int row = 0;
		String boxwidth = "600px";
		boolean TLS = true;

		HTML nameLabel = new HTML(HtmlMarkup.bold("System Name"));
		actorEditGrid.setWidget(row, 0, nameLabel);

		String currentTestSession = ClientUtils.INSTANCE.getCurrentTestSession().getValue();
		String owner = site.getOwner();
		String siteTs = site.getTestSession().getValue();
		if (PasswordManagement.isSignedIn)
			;
		else if (currentTestSession.equals(owner))
			owner = "This Test Session";
		else if (TestSession.DEFAULT_TEST_SESSION.getValue().equals(owner))
			owner = "System";
		else if (TestSession.GAZELLE_TEST_SESSION.getValue().equals(owner))
			owner = TestSession.GAZELLE_TEST_SESSION.getValue();
		else
			owner = "*****";

		Widget ownerWidget;
		if (PasswordManagement.isSignedIn) {
			if (siteTs.equals(TestSession.DEFAULT_TEST_SESSION.getValue())) {
				// tool can manipulate owner if site resides in SYSTEM
				// it would be a technical conflict otherwise
				HorizontalFlowPanel tsPanel = new HorizontalFlowPanel();
				tsPanel.add(new HTML("Owner:"));
				ListBox listBox = new ListBox();
				tsPanel.add(listBox);

				List<String> testSessionNames = ClientUtils.INSTANCE.getTestSessionManager().getTestSessions();
				if (!testSessionNames.contains(TestSession.GAZELLE_TEST_SESSION.getValue()))
					testSessionNames.add(TestSession.GAZELLE_TEST_SESSION.getValue());

				int selection = -1;
				int i = 0;
				for (String ts : ClientUtils.INSTANCE.getTestSessionManager().getTestSessions()) {
					listBox.addItem(ts);
					if (ts.equals(owner))
						selection = i;
					i++;
				}
				listBox.setSelectedIndex(selection);
				listBox.addChangeHandler(new TestSessionChoose(this, listBox));
				ownerWidget = tsPanel;
			} else {
				ownerWidget = new HTML(HtmlMarkup.bold("Owner: " + owner));
			}
		} else {
			ownerWidget = new HTML(HtmlMarkup.bold("Owner: " + owner));
		}
		actorEditGrid.setWidget(row, 2, ownerWidget);

		TextBox nameBox = new TextBox();
		nameBox.setWidth("200px");
		nameBox.setText(trim(currentEditSite.getName()));
		nameBox.addChangeHandler(new NameChangedHandler(this, currentEditSite, nameBox));
		actorEditGrid.setWidget(row, 1, nameBox);

		row++;

		HTML leftHeader = new HTML(HtmlMarkup.bold(getTlsLabel(booleanValues().get(0)) + " Endpoints"));
		HTML rightHeader = new HTML(HtmlMarkup.bold(getTlsLabel(booleanValues().get(1)) + " Endpoints"));
		leftHeader.addStyleName("detail-table-header");
		rightHeader.addStyleName("detail-table-header");
		actorEditGrid.setWidget(row, 1, leftHeader);
		actorEditGrid.setWidget(row, 2, rightHeader);
		row++;

		for (ActorType actorType : TransactionCollection.getActorTypes()) {

			// These actor types need not be shown in the configuration page. Showing these actors causes redundant endpoints in the configuration UI page.
			if (ActorType.REPOSITORY_REGISTRY.equals(actorType)
					|| ActorType.COMBINED_INITIATING_GATEWAY.equals(actorType)
					|| ActorType.COMBINED_RESPONDING_GATEWAY.equals(actorType)
					|| ActorType.OD_RESPONDING_GATEWAY.equals(actorType)
					|| ActorType.FIXED_REPLY.equals(actorType)
					|| ActorType.FHIR_SERVER.equals(actorType) // MHD combines both the FHIR Server and the PDB extension so the basic FHIR (base address) will be made available as part of MHD(displayed in the UI, see the label change below, as FHIR Server).
					|| (!site.isSimulator() && ActorType.ONDEMAND_DOCUMENT_SOURCE.equals(actorType)))
				continue;

			if (actorType.hasTransaction(TransactionType.NONE))
				continue;

			// These getRetrievedDocumentsModel configured in other ways
			String actorTypeName = actorType.getName();
			HTML actorTypeLabel = new HTML(HtmlMarkup.bold(actorTypeName));
			actorEditGrid.setWidget(row, 1, actorTypeLabel);
			row++;

			if (ActorType.MHD_DOC_RECIPIENT.equals(actorType)) {
				actorTypeLabel.setHTML(HtmlMarkup.bold(ActorType.FHIR_SERVER.getName()));
			}

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


			row = addRepositorySection(row, boxwidth, TLS, actorType);
			if (site.isSimulator()) {
				row = addOnDemandRepositorySection(row, boxwidth, TLS, actorType); // This should be a read-only field. We can only make a this distinction for a simulator. For real SUT of ODDS type, they are configured the same way as a repository so this section is hidden if the site.isSimulator flag is False.
			}

			if (ActorType.REGISTRY.equals(actorType)) {
				HorizontalPanel hpanel = new HorizontalPanel();
				Label pifLabel = new Label("Patient Identity Feed");
				actorEditGrid.setWidget(row, 0, pifLabel);

				hpanel.add(new Label("host"));
				TextBox hostbox = new TextBox();
				hostbox.setWidth("300px");
				hostbox.setText(site.pifHost);
				hostbox.addValueChangeHandler(new PifHostChangedHandler(this, currentEditSite, hostbox));
				hpanel.add(hostbox);

				hpanel.add(new Label("port"));
				TextBox portbox = new TextBox();
				portbox.setWidth("100px");
				portbox.setText(site.pifPort);
				portbox.addValueChangeHandler(new PifPortChangedHandler(this, currentEditSite, portbox));
				hpanel.add(portbox);

				actorEditGrid.setWidget(row, 2, hpanel);

				row++;
			}

			for (TransactionType transType : actorType.getTransactions()) {
				if (transType == TransactionType.RETRIEVE)
					continue;   // Handled above
				if (transType == TransactionType.NONE)
					continue;
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
			for (TransactionType transType : actorType.getHTTPTransactions()) {
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

			actorEditGrid.setWidget(row, 0, new HTML("Enable Timestamp Proxy"));
			CheckBox proxyCheckBox = new CheckBox();
			actorEditGrid.setWidget(row, 1, proxyCheckBox);
			proxyCheckBox.setValue(site.useTimestampProxy);
			proxyCheckBox.addValueChangeHandler(new UseTimestampProxyChangedHandler(this, site, proxyCheckBox));

			row++;


//		Button saveButton = new Button("Save Changes");
//		saveButton.addClickHandler(new SaveButtonClickHandler(this));
//		actorEditGrid.setWidget(row, 0, saveButton);
//
//		Button forgetButton = new Button("Forget Changes");
//		forgetButton.addClickHandler(new ForgetButtonClickHandler(this));
//		actorEditGrid.setWidget(row, 1, forgetButton);
//
//		reloadFromGazelleButton = new Button("Reload from Gazelle");
//		reloadFromGazelleButton.addClickHandler(new ReloadSystemFromGazelleClickHandler(this));
//		actorEditGrid.setWidget(row, 2, reloadFromGazelleButton);
//		reloadFromGazelleButton.setEnabled(enableGazelleReload);

	}

	private int addOnDemandRepositorySection(int row, String boxwidth, boolean TLS, ActorType actorType) {
		if (ActorType.ONDEMAND_DOCUMENT_SOURCE.equals(actorType)) {
			Label repuidLabel = new Label("ODDS repositoryUniqueId");
			actorEditGrid.setWidget(row, 0, repuidLabel);

			TextBox repuidBox = new TextBox();
			repuidBox.setWidth(boxwidth);

			String repuid = "";

			boolean isAsync = false;

			TransactionBean repBean = currentEditSite.getRepositoryBean(RepositoryType.ODDS, false);
			TransactionBean secureRepBean = currentEditSite.getRepositoryBean(RepositoryType.ODDS, true);
			TransactionBean transBean = currentEditSite.transactions().find(TransactionType.ODDS_RETRIEVE, false, isAsync);
			TransactionBean secureTransBean = currentEditSite.transactions().find(TransactionType.ODDS_RETRIEVE, true, isAsync);

			if (repBean == null) {
				repBean = new TransactionBean("", RepositoryType.ODDS, "", !TLS, isAsync);
				currentEditSite.addRepository(repBean);
			} else {
				repuid = trim(repBean.getName());
			}

			if (secureRepBean == null) {
				secureRepBean = new TransactionBean("", RepositoryType.ODDS, "", TLS, isAsync);
				currentEditSite.addRepository(secureRepBean);
			} else if (repuid.equals("")) {
				repuid = trim(secureRepBean.getName());
			}

			if (transBean == null) {
				transBean = new TransactionBean(TransactionType.ODDS_RETRIEVE, RepositoryType.ODDS, "", !TLS, isAsync);
				currentEditSite.addTransaction(transBean);
			}

			if (secureTransBean == null) {
				secureTransBean = new TransactionBean(TransactionType.ODDS_RETRIEVE, RepositoryType.ODDS, "", TLS, isAsync);
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

			row++;

		}
		return row;
	}

	private int addRepositorySection(int row, String boxwidth, boolean TLS, ActorType actorType) {
		if (ActorType.REPOSITORY.equals(actorType)) {
            Label repuidLabel = new Label("repositoryUniqueId");
            actorEditGrid.setWidget(row, 0, repuidLabel);

            TextBox repuidBox = new TextBox();
            repuidBox.setWidth(boxwidth);

            String repuid = "";

            boolean isAsync = false;

            TransactionBean repBean = currentEditSite.getRepositoryBean(RepositoryType.REPOSITORY, false);
            TransactionBean secureRepBean = currentEditSite.getRepositoryBean(RepositoryType.REPOSITORY, true);
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
		return row;
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
			newActorEditGrid();
			saveSite();
		}

	};

	private void saveSite() {
		new SaveSiteCommand(){

			@Override
			public void onComplete(String result) {
				currentEditSite.changed = false;
				new GetSiteNamesCommand(){

					@Override
					public void onComplete(List<String> result) {
						loadSiteNames(result);
					}
				}.run(new GetSiteNamesRequest(getCommandContext(),true,showSims.getValue()));
			}
		}.run(new SaveSiteRequest(getCommandContext().withTestSession(currentEditSite.getTestSession().getValue()),currentEditSite));
        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
	}

	void loadExternalSites() {
		GWT.log("loadExternalSites");

		new GetSiteNamesCommand(){

			@Override
			public void onComplete(List<String> result) {
				int allRepos = -1;
				int i = 0;
				for (String s : result) {
					if (s.endsWith("allRepositories")) {
						allRepos = i;
						break;
					}
					i++;
				}
				if (i != -1)
					result.remove(i);
				loadSiteNames(result);
			}
		}.run(new GetSiteNamesRequest(getCommandContext(),true,showSims.getValue()).withQualified(PasswordManagement.isSignedIn));
	}

	void loadSiteNames(List<String> result) {
		newActorEditGrid();

		siteSelector.clear();
		List<String> swapped = swapParts(result);
		for (String site : StringSort.sort(swapped)) {
			if (site.equals("allRepositories"))
				continue;
			siteSelector.addItem(swapParts(site));
		}
		siteSelector.addClickHandler(new SiteChoose(ActorConfigTab.this));

		currentSiteNames = result;
	}

	List<String> swapParts(List<String> siteTags) {
		List<String> work = new ArrayList<>();
		for (String tag : siteTags) {
			String[] parts = tag.split(":");
			if (parts.length == 2) {
				work.add(parts[1] + ":" + parts[0]);
			} else {
				work.add(tag);
			}
		}
		return work;
	}

	String swapParts(String tag) {
		String[] parts = tag.split(":");
		if (parts.length == 2)
			return parts[1] + ":" + parts[0];
		return tag;
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
