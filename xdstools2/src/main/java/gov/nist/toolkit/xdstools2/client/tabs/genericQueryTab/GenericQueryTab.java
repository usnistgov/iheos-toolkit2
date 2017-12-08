package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.registrymetadata.client.AnyId;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.StringSort;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdsexception.client.TkActorNotFoundException;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.GazelleXuaUsername;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.command.GetStsSamlAssertionCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetToolkitPropertiesCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;
import gov.nist.toolkit.xdstools2.client.event.ActorConfigUpdatedEvent;
import gov.nist.toolkit.xdstools2.client.event.EnvironmentChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.SimulatorUpdatedEvent;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.ResultInspector;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab.ActorConfigTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.NewToolLauncher;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.InformationLink;
import gov.nist.toolkit.xdstools2.client.widgets.PidWidget;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetStsSamlAssertionRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Infrastructure for any tab that will allow a site to be chosen,
 * issue a transaction, getRetrievedDocumentsModel back results,
 * and allow the results to be inspected
 * @author bill
 */
public abstract class GenericQueryTab  extends ToolWindow {
    GenericQueryTab me;
    private final SiteLoader siteLoader = new SiteLoader(this);
    static public TransactionOfferings transactionOfferings = null;  // Loaded from server

    protected FlexTable mainGrid;
    public TabContainer myContainer;
    public int row_initial;
    int row;

    public boolean tlsEnabled = true;
    public boolean tlsOptionEnabled = true;
    public ActorType selectByActor = null;
    /**
     * The declared transactions to target by the tool.
     */
    List<TransactionType> transactionTypes;
    public TransactionSelectionManager transactionSelectionManager = null;
    public boolean enableInspectResults = true;
    CoupledTransactions couplings;

    public boolean runEnabled = true;
    ClickHandler runner;
    String runButtonText = "Run";
    HorizontalPanel runnerPanel = new HorizontalPanel();

    public VerticalPanel resultPanel = new VerticalPanel();
    // if false then tool takes responsibliity for placing it
    public boolean addResultsPanel = true;

    CheckBox doTls = new CheckBox("TLS?");
    ListBox samlListBox = new ListBox();
    List<RadioButton> byActorButtons = null;
    //	public Map<TransactionType, List<RadioButton>> perTransTypeRadioButtons;

    private Button inspectButton;
    private Button goButton;

    boolean showInspectButton = true;
    boolean asyncEnabled = false;
    public boolean doASYNC = false;
    boolean hasPatientIdParam = false;

    BaseSiteActorManager siteActorManager;// = new SiteActorManager(this);
    HTML resultsShortDescription = new HTML();
    public boolean autoAddRunnerButtons = true;
    public String genericQueryTitle = null;
    public Widget genericQueryInstructions = null;

    public String selectedTest;
    List<Result> results;

    protected QueryBoilerplate queryBoilerplate = null;

    HTML statusBox = new HTML();
    public PidWidget pidTextBox = new PidWidget();

    HorizontalPanel logLaunchButtonPanel = new HorizontalPanel();
    Button runButton = new Button(runButtonText);
    Button inspectButon = new Button("Inspect Results");
    HandlerRegistration inspectButtonHandler = null;

    Widget mainConfigPanelDivider = null;
    VerticalPanel mainConfigPanel = null;

    Anchor reload = null;
    private Widget widget;

    private boolean displayTab = true;
    private List<TransactionSelectionManager.RbSite> selectedSites=new ArrayList<>();

    // Keep SAML fields off the base class to isolate other tools' preference
    public boolean samlEnabled = true;
    public String samlAssertion;

    /**
     * This is the method that should build the specific content of a tab.
     * This method should mostly only contain GWT client widget.
     * @return UI of the tab as a Widget
     */
    protected abstract Widget buildUI();

    /**
     * This is the method that should bind the tab's widgets with actions, the eventbus and the server.
     * This methoud could contains eventbus handlers, calls to the server or even action handlers like click handler,
     * valuechange handler...
     */
    protected abstract void bindUI();

    /**
     * This is the method where all the UI configurations though call to existing method in GenericQueryTab should be.
     * It is mostly used for {@link #addQueryBoilerplate} methods so far.
     */
    protected abstract void configureTabView();

    /**
     * Super constructor.
     * @param siteActorManager
     */
    public GenericQueryTab(BaseSiteActorManager siteActorManager) {
        this(siteActorManager, 0.0, 0.0);
    }

    public GenericQueryTab(BaseSiteActorManager siteActorManager, double east, double west) {
        super(east, west);
        me = this;
        this.siteActorManager = siteActorManager;
        if (siteActorManager != null)
            siteActorManager.setGenericQueryTab(this);
        bind();
    }

    private void bind(){
        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
                    reloadTransactionOfferings();
                    refreshData();
                }
            }
        });
        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).addEnvironmentChangedEventHandler(new EnvironmentChangedEvent.EnvironmentChangedEventHandler() {
            @Override
            public void onEnvironmentChange(EnvironmentChangedEvent event) {
                reloadTransactionOfferings();
                refreshData();
            }
        });

        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).addSimulatorsUpdatedEventHandler(new SimulatorUpdatedEvent.SimulatorUpdatedEventHandler() {
            @Override
            public void onSimulatorsUpdate(SimulatorUpdatedEvent simulatorUpdatedEvent) {
                saveSelectedSites();
                reloadTransactionOfferings();
            }
        });

        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).addActorsConfigUpdatedEventHandler(new ActorConfigUpdatedEvent.ActorConfigUpdatedEventHandler() {
            @Override
            public void onActorsConfigUpdate() {
                if(!tabName.equals(ActorConfigTab.TAB_NAME)) {
                    saveSelectedSites();
                    reloadTransactionOfferings();
                }
            }
        });
    }

    private void saveSelectedSites() {
        selectedSites.clear();
        if (selectByActor != null) {    // Used in Mesa test tab
            GWT.log("Mesa tab site selection - TODO?");
            /*
            for (RadioButton b : byActorButtons) {
                if (b.getValue()) {
                    selectedSites.add(b.getText());
                }
            }
            */
        } else {   // Select by transaction (used in GetDocuments tab)
            if (transactionSelectionManager != null) {
                List<TransactionSelectionManager.RbSite> rbSites = transactionSelectionManager.selections2();
                if (rbSites != null) {
                    selectedSites.addAll(rbSites);
                }
            }
        }
    }

    @Override
    public void onTabLoad(boolean select, String eventName) {
        if (displayTab)
            registerTab(true, eventName);
        buildView();  // the view is still built because of old code in HomeTab -
    }

    protected void buildView(){
        widget=buildUI();
        if (widget != null) {
            tabTopPanel.add(widget);
            configureTabView();
            bindUI();
        }
    }

    private void refreshData(){
        bindUI();
    }

    // clean out mainGrid so the actors can be re-added
    public void initMainGrid() {
        if (mainGrid == null) {
            mainGrid = new FlexTable();
            tabTopPanel.add(mainGrid);
        }
        while (mainGrid.getRowCount() > row_initial)
            mainGrid.removeRow(mainGrid.getRowCount() - 1);
        row = row_initial;
    }
/*
    void resdisplay2(SiteSpec siteSpec) {
        redisplay(false);
        transactionSelectionManager.selectSite(siteSpec);
    }
*/
    // TODO this is a big method, try to figure out what it is for
    public void redisplay(boolean clearResults) {
        if (resultPanel != null && clearResults)
            resultPanel.clear();
        initMainGrid();

        if (mainConfigPanel == null)
            return;

        mainConfigPanel.clear();

        // two columns - title and contents
        int titleColumn = 0;
        int contentsColumn = 1;
        int commonGridRow = 0;

        if (genericQueryTitle != null) {
            mainConfigPanel.add(new HTML("<h2>" + genericQueryTitle + "</h2>"));
        }

        if (genericQueryInstructions != null) {
            mainConfigPanel.add(genericQueryInstructions);
        }

        final FlexTable commonParamGrid = new FlexTable();
        mainConfigPanel.add(commonParamGrid);

        if (hasPatientIdParam) {
            commonParamGrid.setWidget(commonGridRow, titleColumn, new HTML("Patient ID"));
            HTMLTable.CellFormatter formatter = commonParamGrid.getCellFormatter();
            formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
            formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

            pidTextBox.setWidth("500px");
//            pidTextBox.setText(getCommonPatientId());
            pidTextBox.addChangeHandler(new PidChangeHandler(this));
            commonParamGrid.setWidget(commonGridRow++, contentsColumn, pidTextBox);
        }

        SiteSpec commonSiteSpec = null;
        commonSiteSpec = getCommonSiteSpec();

        final FlowPanel fp = new FlowPanel();
        final HTML samlLabel = new HTML("SAML");
        commonParamGrid.setWidget(commonGridRow, titleColumn, samlLabel);

        samlListBox = new ListBox();
        samlListBox.addItem("SAML OFF", "NoSaml");
        for (GazelleXuaUsername username : GazelleXuaUsername.values()) {
            samlListBox.addItem("SAML - " + username.name(), username.name());
        }
        samlListBox.setVisibleItemCount(1);
        samlListBox.addChangeHandler(new SamlSelector(this));
        if (commonSiteSpec != null)
            samlListBox.setSelectedIndex((commonSiteSpec.isSaml) ? 1 : 0);

        commonParamGrid.setWidget(commonGridRow++, contentsColumn, fp);

        new GetToolkitPropertiesCommand(){
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Error getting properties for SAML selector display: " + throwable.toString());
                fp.setVisible(false);
                samlLabel.setVisible(false);
            }
            @Override
            public void onComplete(final Map<String, String> tkPropMap) {
                samlEnabled = Boolean.parseBoolean(tkPropMap.get("Enable_SAML"));

                if (samlEnabled) {
                    Anchor gazelleXuaTablePage = new Anchor("Assertions for Connectathon");
                    gazelleXuaTablePage.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            // Check URL safety?
                            Window.open(tkPropMap.get("Gazelle_Assertions_for_Connectathon_URL"), "_blank","");
                        }
                    });
                    gazelleXuaTablePage.setTitle("Opens the Gazelle XUA Username table in a new browser tab.");
                    gazelleXuaTablePage.getElement().getStyle().setMarginLeft(7, Style.Unit.PX);

                    samlListBox.setSelectedIndex(0);
                    fp.add(samlListBox);
                    fp.add(gazelleXuaTablePage);

                    TransactionType stsTran = TransactionType.STS;
                    List<Site> sites = siteLoader.findSites(stsTran, true);
                    if (sites==null || (sites!=null && sites.size()==0)) {
                        samlListBox.setSelectedIndex(0);
                        samlListBox.setEnabled(false);
                        fp.add(new HTML("<span class=\"serverResponseLabelError\">GazelleSts site is not configured. Please check External Cache Actor site configuration.</span>"));
                    }
                    fp.setVisible(true);
                    samlLabel.setVisible(true);
                } else {
                    fp.setVisible(false);
                    samlLabel.setVisible(false);
                }
            }
        }.run(getCommandContext());



        if (tlsEnabled) {
            doTls = new CheckBox("use TLS");
            doTls.setEnabled(tlsOptionEnabled);
            if (getCommonSiteSpec() != null) {
                doTls.setValue(getCommonSiteSpec().isTls());
            }
            doTls.addClickHandler(new TlsSelector(this));
//			commonParamGrid.setWidget(commonGridRow, titleColumn, new HTML("TLS"));
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
            FlexTable siteSelectionPanel = new FlexTable();
            siteSelectionPanel.getFlexCellFormatter().setVerticalAlignment(0,0,HasVerticalAlignment.ALIGN_TOP);
            siteSelectionPanel.setWidget(0, 0, new HTML("<div style='margin-top:2px;font-size:1.1em;'>Send to</div>"));

//            Image infoImage = new Image("icons/info.png");
//            infoImage.setTitle("Help with system selection");
//            infoImage.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent clickEvent) {
//                    Window.open(Xdstools2.wikiBaseUrl + "/System-selection-issues", "_blank","");
//                }
//            });
//
//            siteSelectionPanel.setWidget(0, 1, infoImage);

            siteSelectionPanel.setWidget(0, 1, new InformationLink("Help with system selection", "System-selection-issues").asWidget());

            FlexTable siteGrid = new FlexTable();
            siteSelectionPanel.getFlexCellFormatter().setVerticalAlignment(0,1,HasVerticalAlignment.ALIGN_TOP);
            siteSelectionPanel.setWidget(1, 1, siteGrid);

            int siteGridRow = 1;
            /*
            Set<String> actorTypeNamesAlreadyDisplayed = new HashSet<>();
            for (TransactionType tt : transactionTypes) {
                Set<ActorType> ats = ActorType.getActorTypes(tt);
                for (ActorType at : ats) {
                    String actorTypeName = at.getName();
                    if (!actorTypeNamesAlreadyDisplayed.contains(actorTypeName) && at.showInConfig()) {
                        actorTypeNamesAlreadyDisplayed.add(actorTypeName);
                        Label label = new Label(at.getName() + ":");
                        label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLDER);
                        siteGrid.setWidget(siteGridRow, 0, label);
                        if (getSiteTableForTransactionsSize(tt) == 0) {
                            siteGrid.setWidget(siteGridRow++, 1, new Label("None Available"));
                        } else {
                            siteGrid.setWidget(siteGridRow++, 1, getSiteTableWidgetforTransactions(at,tt));
                        }
                    }
                }
            }
            */

            Map<ActorType,ActorSitesByTran> actorSiteMap = createSiteSelectionByTransactionList();

            if (actorSiteMap.isEmpty()) {
                siteGrid.setWidget(siteGridRow, 0, new HTML("&nbsp;")); // spacer
                siteGrid.setWidget(siteGridRow, 1, new HTML("None available"));
                siteGridRow++;
            } else {
                for (ActorType at : ActorType.values()) {
                if (actorSiteMap.containsKey(at)) {
                    if (at.showInConfig()) { // Exclude types like the "Any" ActorType
                        Label label = new Label(at.getName() + ":");
                        label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLDER);
                        siteGrid.setWidget(siteGridRow, 0, label);
                        ActorSitesByTran actorSitesByTran = actorSiteMap.get(at);
                        siteGrid.setWidget(siteGridRow++, 1, getSiteTableWidgetforTransactions(at, actorSitesByTran.transactionType, actorSitesByTran.sites));
                        if (couplings!=null && couplings.hasCouplings()) {
                            if (ActorType.INITIATING_GATEWAY.equals(at) && actorSitesByTran.transactionType == couplings.from() && couplings.to()!=null) {
                                HTML instruction = (HTML)couplings.getCoupling().getSelectionInstructions();
                                if (instruction!=null) {
                                    instruction.setVisible(false); // Should be enabled only when the From site is selected
                                    instruction.addStyleName("serverResponseLabelError");
                                    siteGrid.setWidget(siteGridRow, 0, new HTML(" ")); // spacer
                                    siteGrid.setWidget(siteGridRow, 1, instruction);
                                    siteGrid.getFlexCellFormatter().setHorizontalAlignment(0,1, HasHorizontalAlignment.ALIGN_RIGHT);
//                                    siteGrid.getFlexCellFormatter().setColSpan(siteGridRow, 0, 2);
                                    siteGridRow++;
                                }
                            }
                        }
                    }
                }
            }
                if (couplings!=null && couplings.hasCouplings()) {
                    siteGrid.setWidget(siteGridRow, 0, new HTML("&nbsp;")); // spacer
                     Button clearSelectionBtn = new Button("Clear");
                    clearSelectionBtn.getElement().getStyle().setMarginLeft(6, Style.Unit.PX);
                    clearSelectionBtn.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        transactionSelectionManager.turnOffButtonsNotIn(null);
                        HTML instruction = (HTML) couplings.getCoupling().getSelectionInstructions();
                        if (instruction != null) {
                            instruction.setVisible(false);
                        }
                    }
                    });
                    siteGrid.setWidget(siteGridRow, 1, clearSelectionBtn);
                    siteGridRow++;
                }
            }

            DecoratorPanel decoration = new DecoratorPanel();
            decoration.setStyleName("queryBoilerPlate");
            decoration.add(siteSelectionPanel);
            mainConfigPanel.add(decoration);
        }
        if (autoAddRunnerButtons)
            addRunnerButtons(mainConfigPanel);

        // Go/Run button should be enabled when a site is selected, so here it is initially disabled.
        if (getGoButton()!=null)
            getGoButton().setEnabled(false);

    }

    Map<ActorType,ActorSitesByTran> createSiteSelectionByTransactionList() {
       Map<ActorType, ActorSitesByTran> actorSitesMap = new HashMap<>();
        for (TransactionType tt : transactionTypes) { /* declared transaction types by the tool */
            List<Site> siteList = getSiteList(tt);
            if (siteList!=null && !siteList.isEmpty()) {
               for (Site site : siteList)  {
                   try {
                       ActorType actorType = site.determineActorTypeByTransactionsInSite(tt);
                       if (!actorSitesMap.containsKey(actorType)) {
                           actorSitesMap.put(actorType, new ActorSitesByTran(tt));
                       }
                       actorSitesMap.get(actorType).sites.add(site);
                   } catch (TkActorNotFoundException ex) {
                      continue;
                   }
               }
            }
        }
        return actorSitesMap;
    }

    public void tabIsSelected() {
        System.out.println("tab selected: " + getCommonSiteSpec());

        doTls.setValue(getCommonSiteSpec().isTls());
        samlListBox.setSelectedIndex((getCommonSiteSpec().isSaml) ? 1 : 0);
        if (pidTextBox != null)
            pidTextBox.setText(getCommonPatientId());
    }

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
        return addQueryBoilerplate(runner, transactionTypes, couplings, true);
    }

    public QueryBoilerplate addQueryBoilerplate(ClickHandler runner, List<TransactionType> transactionTypes,
                                                CoupledTransactions couplings, boolean hasPatientIdParam) {
        if (queryBoilerplate != null) {
            queryBoilerplate.remove();
            queryBoilerplate = null;
        }
        this.hasPatientIdParam = hasPatientIdParam;

        if (mainConfigPanelDivider == null) {
            mainConfigPanelDivider = new HTML("<hr />");
            tabTopPanel.add(mainConfigPanelDivider);
            mainConfigPanel = new VerticalPanel();
            tabTopPanel.add(mainConfigPanel);
            tabTopPanel.add(new HTML("<hr />"));
        }
        if (addResultsPanel)
            tabTopPanel.add(resultPanel);
        queryBoilerplate = new QueryBoilerplate(
                this, runner, transactionTypes,
                couplings
        );
        return queryBoilerplate;
    }

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

    /**
     * Call on backend to reload transactions (simulators).
     */
    public void reloadTransactionOfferings() {
        new GetTransactionOfferingsCommand() {

            @Override
            public void onComplete(TransactionOfferings var1) {
                GenericQueryTab.transactionOfferings = var1;
                redisplay(false);
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());
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

    String red(String msg, boolean status) {
        if (status)
            return msg;
        return HtmlMarkup.red(msg);
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
        tabTopPanel.add(msgBox);
    }

    public void setStatus(String message, boolean status) {
        statusBox.setHTML(HtmlMarkup.bold(red(message,status)));
    }

    public void setStatus(String message) {
        statusBox.setHTML(message);
    }


    public void addStatusBox() {
        addStatusBox(getRunningMessage());
    }

    public void addStatusBox(String initialMessage) {
        setStatus(initialMessage, true);
        resultPanel.add(statusBox);
    }

    public void prepareToRun() {
        addStatusBox();
        getGoButton().setEnabled(false);
        getInspectButton().setEnabled(false);
    }

    public void addRunnerButtons(Panel panel) {
        boolean hasRunButton = runnerPanel.getWidgetIndex(runButton) > -1;

        panel.add(runnerPanel);
        if (runEnabled) {
            setGoButton(runButton);
            runnerPanel.add(getGoButton());
        }


        try {
            if (!hasRunButton) {
                getGoButton().addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(final ClickEvent clickEvent) {

                        String selectedValue = samlListBox.getSelectedValue();
                        if (samlEnabled && !"NoSaml".equals(selectedValue)) {
                            TestInstance testInstance = new TestInstance("GazelleSts");
                            testInstance.setSection("samlassertion-issue");
                            SiteSpec stsSpec =  new SiteSpec("GazelleSts");
                            Map<String, String> params = new HashMap<>();
                            params.put("$saml-username$",selectedValue);
                            new GetStsSamlAssertionCommand(){
                                @Override
                                public void onFailure(Throwable throwable) {
                                    SafeHtmlBuilder shb = new SafeHtmlBuilder();
                                    shb.appendHtmlConstant("Error");
                                    resultPanel.clear();
                                    addStatusBox("");
                                    setStatus("Status: Failure",false);
                                    resultPanel.add(new HTML(throwable.toString()));
                                }
                                @Override
                                public void onComplete(String result) {
                                    samlAssertion = result;
                                    runner.onClick(clickEvent);
                                }
                            }.run(new GetStsSamlAssertionRequest(getCommandContext(),selectedValue,testInstance,stsSpec,params));
                        } else {
                            runner.onClick(clickEvent);
                        }
                    }
                });
            }
        } catch (Exception e) {}

        if (enableInspectResults) {
            setInspectButton(inspectButon);
            getInspectButton().setEnabled(false);
            runnerPanel.add(getInspectButton());
        }

        if (getInspectButton() != null && inspectButtonHandler == null) {
//            inspectButtonHandler = getInspectButton().addClickHandler(new InspectorLauncher(me));
           inspectButtonHandler = getInspectButton().addClickHandler(new ClickHandler() {
               @Override
               public void onClick(ClickEvent clickEvent) {
                   ResultInspector resultInspector = new ResultInspector();
                   resultInspector.setResults(results);
                   resultInspector.setSiteSpec(getSiteSelection());
                   new NewToolLauncher().launch(resultInspector);
               }
           });
        }


        runnerPanel.add(logLaunchButtonPanel);

        resultsShortDescription.setHTML("");
        runnerPanel.add(resultsShortDescription);
    }


    // since to has come over from server and tt was generated here, they
    // don't align hashvalues.  Search must be done the old fashion way
    List<Site> findSites(TransactionType tt, boolean tls) {

        // aka testSession

        return siteLoader.findSites(tt, tls);
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

    protected boolean verifyPidProvided() {
        if (pidTextBox.getValue() == null || pidTextBox.getValue().equals("")) {
            new PopupMessage("You must enter a Patient ID first");
            return false;
        }
        return true;
    }

    public boolean verifySiteProvided() {
        SiteSpec siteSpec = getSiteSelection();
        if (siteSpec == null) {
            new PopupMessage("You must select a site first");
            return false;
        }
        return true;
    }

    protected HorizontalPanel rigForRunning() {
        resultPanel.clear();
        // Where the bottom-of-screen listing from server goes
        addStatusBox();
        getGoButton().setEnabled(false);
        getInspectButton().setEnabled(false);
        return logLaunchButtonPanel;
    }

    public static boolean empty(String x) {
        if (x == null) return true;
        if (x.equals("")) return true;
        return false;
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

    public String getSelectedValueFromListBox(ListBox lb) {
        int i = lb.getSelectedIndex();
        if ( i == -1)
            return null;
        return lb.getValue(i);
    }

    List<Site> getSiteList(TransactionType tt) {
        List<Site> sites = siteLoader.findSites(tt, isTLS());
        List<String> siteNames = new ArrayList<String>();
        for (Site site : sites)
            siteNames.add(site.getName());
        siteNames = StringSort.sort(siteNames);

        List<Site> orderedSites = new ArrayList<Site>();
        for (String siteName : siteNames) {
            for (Site site : sites) {
                if (siteName.equals(site.getName())) {
                    orderedSites.add(site);
                    break;
                }
            }
        }
        return orderedSites;
    }

    Widget getSiteTableWidgetforTransactions(ActorType at, TransactionType tt, List<Site> sites) {
        if (transactionSelectionManager == null)
            transactionSelectionManager = new TransactionSelectionManager(couplings, this);
//        List<Site> sites = getSiteList(tt);


        transactionSelectionManager.addTransactionType(at, tt, sites);

        int cols = 5;
        int row=0;
        int col=0;
        Grid grid = new Grid( sites.size()/cols + 1 , cols);
        for (TransactionSelectionManager.RbSite rbSite : transactionSelectionManager.getPerTransRB(at, tt)) {
            rbSite.rb.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    getGoButton().setEnabled(transactionSelectionManager.verifySelection()==null);
                }
            });
            grid.setWidget(row, col, rbSite.rb);
            if (selectedSites.contains(rbSite)){
                rbSite.rb.setValue(true);
            }
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }
//		mainGrid.setWidget(majorRow, startingCol, grid);
        return grid;
    }


    /////////////////////////////////////////////////////////////////////////
    // - GETTERS AND SETTERS
    /////////////////////////////////////////////////////////////////////////
    public String getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(String selectedTest) {
        this.selectedTest = selectedTest;
    }

    public SiteSpec getSiteSelection() { return queryBoilerplate.getSiteSelection(); }

    int getSiteTableForTransactionsSize(TransactionType tt) {
        return getSiteList(tt).size();
    }

    public void setRunButtonText(String label) {
        runButtonText = label;
    }

    protected TestSessionManager2 getTestSessionManager() {
        return testSessionManager;
    }

    public void setTlsEnabled(boolean value) {
        tlsEnabled = value;
    }

    public void setSamlEnabled(boolean value) {
        samlEnabled = value;
    }

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
        if (selection > 0)
            return true;  // first selection must be no saml
        return false;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        setCommonSiteSpec(siteSpec);
    }

    public QueryBoilerplate getQueryBoilerplate() {
        return queryBoilerplate;
    }

    public String getRunningMessage() {
        return "Running (connection timeout is 30 sec) ...";
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

    /////////////////////////////////////////////////////////////////////////
    // - Internal class implementation
    /////////////////////////////////////////////////////////////////////////
    class DetailsTree {
        TreeItem root;
        Tree tree;
        boolean hasNodes;

        DetailsTree() {
            tree = new Tree();
            root = new TreeItem();
            root.setText("Details...");
            tree.addItem(root);
        }
        void add(String x) { root.addTextItem(x); hasNodes=true;}
        Tree getWidget() { return tree; }
    }

    protected AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {

        public void onFailure(Throwable caught) {
            resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error running validation: " + caught.getMessage() + "</font>"));
            resultsShortDescription.setText("");
        }

        public void onSuccess(List<Result> theresult) {
            displayResults(theresult);
        }

    };

    protected void displayResults(List<Result> theresult) {
        resultsShortDescription.setText("");
        try {
            if (theresult.size() == 1) {
                MetadataCollection mc = theresult.get(0).getStepResults().get(0).getMetadata();
                StringBuilder buf = new StringBuilder();
                buf.append("  ==> ");
                buf.append(mc.submissionSets.size()).append(" SubmissionSets ");
                buf.append(mc.docEntries.size()).append(" DocumentEntries ");
                buf.append(mc.folders.size()).append(" Folders ");
                buf.append(mc.objectRefs.size()).append(" ObjectRefs ");
                if (theresult.get(0).getStepResults().get(0).documents!=null) {
                    buf.append(theresult.get(0).getStepResults().get(0).documents.size()).append(" Documents");
                }
                resultsShortDescription.setText(buf.toString());
            }
        } catch (Exception e) {}
        DetailsTree detailsTree = null;
        boolean status = true;
        boolean partialSuccess = false;
        results = theresult;
        for (Result result : results) {
            if (result.getStepResults().size()>0) {
                if ("urn:ihe:iti:2007:ResponseStatusType:PartialSuccess".equals((result.getStepResults().get(0).getRegistryResponseStatus()))) {
                    partialSuccess = true;
                }
            }
            detailsTree = new DetailsTree();
            for (AssertionResult ar : result.assertions.assertions) {
                if (ar.assertion.startsWith("ReportBuilder") && detailsTree != null) {
                    detailsTree.add(ar.assertion);
                } else if (ar.assertion.startsWith("UseReport") && detailsTree != null) {
                    detailsTree.add(ar.assertion);
                } else if (ar.assertion.startsWith("SOAPFault") && detailsTree != null) {
                    detailsTree.add(ar.assertion);
                } else {
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
                if (ar.assertion.startsWith("Status")) {
                    resultPanel.add(detailsTree.getWidget());
                }
            }
        }
        if (status) {
            if (partialSuccess)
                setStatus("<span style=\"color:orange;font-weight:bold;\">Status:</span>&nbsp;<span style=\"color:orange;font-weight:bold;\">PartialSuccess</span>");
            else
                setStatus("Status: Success", true);
        } else
            setStatus("Status: Failure", false);

        getInspectButton().setEnabled(true);
        getGoButton().setEnabled(true);
    }

    String displayable(String in) {
        return in.replaceAll("\n", "<br />");
    }

    public void setDisplayTab(boolean displayTab) {
        this.displayTab = displayTab;
    }

    private class ActorSitesByTran {
        TransactionType transactionType;
        List<Site> sites = new ArrayList<>();

        public ActorSitesByTran(TransactionType transactionType) {
            this.transactionType = transactionType;
        }

    }

}
