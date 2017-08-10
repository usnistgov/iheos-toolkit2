package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.StringSort;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.event.SimulatorUpdatedEvent;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.*;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.intf.SimConfigMgrIntf;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.*;

import java.util.*;

/**
 *
 * Created by skb1 on 3/24/2016.
 */
public class OddsSimConfigMgr implements SimConfigMgrIntf {

    private SimulatorControlTab simulatorControlTab;
    FlowPanel panel;
    HorizontalPanel hpanel;
    SimulatorConfig config;
    String testSession;
    FlexTable tbl = new FlexTable();

    CheckBox persistenceCb = new CheckBox();
    SingleSelectionView persistenceSsv = new SingleSelectionView();
    HorizontalPanel reposSiteBoxes = new HorizontalPanel();
    SiteSelectionPresenter reposSSP;
    HorizontalPanel regSiteBoxes = new HorizontalPanel();
    SiteSelectionPresenter regSSP;
    ConfigEditBox oddePatientIdCEBox = new ConfigEditBox();
    ConfigEditBox testPlanCEBox = new ConfigEditBox();
    ConfigTextDisplayBox oddsReposTDBox;
    Button regButton = new Button("Initialize"); // Register an On-Demand Document Entry
    HTML regActionMessage = new HTML();
    int row = 0;
    FlexTable oddeEntriesTbl = new FlexTable();
    ListBox contentBundleLbx = new ListBox();
    static String choose = "-- Choose --";
//    Button refreshSupplyState = new Button("<span style=\"font-size:8px;color:blue\">Refresh</span>");

    interface OddsResources extends ClientBundle {
        public static final OddsResources INSTANCE = GWT.create(OddsResources.class);

        @Source("icons/ic_refresh_black_24dp_1x.png")
        ImageResource getRefreshIcon();
    }
    Image refreshImg = new Image(((OddsResources)GWT.create(OddsResources.class)).getRefreshIcon());


    public OddsSimConfigMgr(SimulatorControlTab simulatorControlTab, FlowPanel panel, SimulatorConfig config, String testSession) {

        this.simulatorControlTab = simulatorControlTab;
        this.panel = panel;
        this.config = config;
        this.testSession = testSession;

    }

    public void removeFromPanel() {
        if (hpanel != null) {
            panel.remove(hpanel);
            hpanel = null;
        }
    }
    @Override
    public void displayHeader() {
        getPanel().add(new HTML("<h1>On-Demand Document Source (ODDS) Simulator Configuration</h1>"));

        getPanel().add(new HTML("" +
                "This simulator supports testing of Registration and Retrieval of On-Demand patient documents. First, initialize this simulator by registering an On-Demand Document Entry (ODDE) and then retrieve the On-Demand document." +
                "" +
                "<hr/>"

        ));
    }

    @Override
    public void displayBasicSimulatorConfig() {
        FlexTable tbl = getTbl();

        newRow();
        FlexTable sectionHeaderTbl = new FlexTable();
        sectionHeaderTbl.setWidget(0,0,new HTML("<h2>Simulator Configuration</h2>" ));
        Button saveButton = new Button("Save");
        saveButton.addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        // Only save, no validation takes place here.
                        saveSimConfig();
                    }
                }
        );
        sectionHeaderTbl.setWidget(0, 1, saveButton);
        sectionHeaderTbl.getFlexCellFormatter().setHorizontalAlignment(0,1, HasHorizontalAlignment.ALIGN_RIGHT);
        sectionHeaderTbl.getFlexCellFormatter().setVerticalAlignment(0,1, HasVerticalAlignment.ALIGN_MIDDLE);
        sectionHeaderTbl.setWidth("100%");
        tbl.setWidget(getRow(), 0, sectionHeaderTbl);
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

        newRow();
        tbl.setWidget(getRow(), 0, HtmlMarkup.html("Simulator Type"));
        tbl.setWidget(getRow(), 1, HtmlMarkup.html(config.actorTypeFullName()));

        newRow();
        tbl.setWidget(getRow(), 0, HtmlMarkup.html("Simulator ID"));
        tbl.setWidget(getRow(), 1, HtmlMarkup.html(config.getId().toString()));

        newRow();
        new ConfigTextDisplayBox(config.get(SimulatorProperties.creationTime), tbl, getRow());

        newRow();
        new ConfigTextDisplayBox(config.get("Name") , tbl, getRow());

        newRow();
        new ConfigBooleanBox(config.get(SimulatorProperties.FORCE_FAULT), tbl, getRow());

        newRow();
        new ConfigTextDisplayBox(config.get(SimulatorProperties.environment),tbl, getRow());

        newRow();
        new ConfigTextDisplayBox(config.get(SimulatorProperties.codesEnvironment),tbl, getRow());

    }

    @Override
    public void displayInPanel() {


        // Basic Simulator config
        displayBasicSimulatorConfig();

        // Register config
        displayRegisterOptions();

        // Retrieve config
        displayRetrieveConfig();

        // Supply State
        displaySupplyState();

        // Register event handlers
        registerSimulatorsUpdatedEvent();

    }

    private void registerSimulatorsUpdatedEvent() {
        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).addSimulatorsUpdatedEventHandler(new SimulatorUpdatedEvent.SimulatorUpdatedEventHandler() {
            @Override
            public void onSimulatorsUpdate(SimulatorUpdatedEvent simulatorUpdatedEvent) {

                final SimulatorConfigElement oddsRegistrySite = config.get(SimulatorProperties.oddsRegistrySite);
                popRegSites(oddsRegistrySite);

                final SimulatorConfigElement oddsReposSite = config.get(SimulatorProperties.oddsRepositorySite);
                popRepositorySites(oddsReposSite);
            }
        });
    }

    public void displayPersistenceConfig() {
        SimulatorControlTab simulatorControlTab = getSimulatorControlTab();
        final SimulatorConfig config = getConfig();
        FlexTable tbl = getTbl();
        final HTML lblReposSiteBoxes = HtmlMarkup.html(SimulatorProperties.oddsRepositorySite);

        newRow();
        tbl.setWidget(getRow(), 0, new HTML(""
                + "<h3 style='padding: 0px;margin: 0px;'>Persistence Option</h3>"
                + "<p>If you choose to enable the Persistence Option checking the box below, please select a Repository which should be configured to forward registry requests to the same Registry site where you intend to register this ODDE.</p>" ));
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

        final SimulatorConfigElement persistenceOption = config.get(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS);
        if (persistenceOption!=null) {
            newRow();

            tbl.setText(getRow(), 0, persistenceOption.name.replace('_', ' '));

//            List<String> pOtpn = new ArrayList<>();
//            pOtpn.display("Off");
//            pOtpn.display("On");
//            persistenceSsv.setData("persistenceOptn",pOtpn);
//            List<Integer> selectedRow = new ArrayList<>();
//            if (persistenceOption.asBoolean())
//                selectedRow.display(1);
//            else
//                selectedRow.display(0);
//            persistenceSsv.setSelectedRows(selectedRow);
//            tbl.setWidget(getRow(), 1, persistenceSsv.asWidget());

            persistenceCb.setValue(persistenceOption.asBoolean());
            persistenceCb.setEnabled(persistenceOption.isEditable());
            tbl.setWidget(getRow(), 1, persistenceCb);
            persistenceCb.addClickHandler(
                    new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            persistenceOption.setBooleanValue(persistenceCb.getValue());

                            boolean persistenceOpt = persistenceCb.getValue();
                            lblReposSiteBoxes.setVisible(persistenceOpt);
                            reposSiteBoxes.setVisible(persistenceOpt);
//                                    oddePatientIdCEBox.setVisible(persistenceOpt);
//                                    testPlanCEBox.setVisible(persistenceOpt);

                        }
                    }
            );
        }


        final SimulatorConfigElement oddsReposSite = config.get(SimulatorProperties.oddsRepositorySite);
        if (oddsReposSite!=null) {
            popRepositorySites(oddsReposSite);


            newRow();
            tbl.setWidget(getRow(), 0, lblReposSiteBoxes);
            tbl.setWidget(getRow(), 1, reposSiteBoxes);

            boolean persistenceOpt = persistenceCb.getValue();
            lblReposSiteBoxes.setVisible(persistenceOpt);
            reposSiteBoxes.setVisible(persistenceOpt);


        }

    }

    private void popRepositorySites(final SimulatorConfigElement oddsReposSite) {
        if (oddsReposSite!=null) {
            // Selecting a Repository for the ODDS
            new GetSiteNamesByTranTypeCommand() {
                @Override
                public void onComplete(List<String> results) {
                    reposSiteBoxes.clear();
                    reposSSP = new SiteSelectionPresenter("reposSites", results, oddsReposSite.asList(), reposSiteBoxes);
                }
            }.run(new GetSiteNamesByTranTypeRequest(ClientUtils.INSTANCE.getCommandContext(), TransactionType.PROVIDE_AND_REGISTER.getName()));
            // ----
        }
    }

    public void displayRetrieveConfig() {
        SimulatorControlTab simulatorControlTab = getSimulatorControlTab();
        final SimulatorConfig config = getConfig();
        FlexTable tbl = getTbl();


        // Retrieve config
        newRow();
        tbl.setWidget(getRow(), 0, new HTML("<hr/>"
                + "<h2>Retrieve Configuration</h2>"
                + "<p>A Document Consumer may retrieve an On-Demand document from an ODDS using a DocumentUniqueId and a RepositoryUniqueId.</p>" ));
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);


        for (final SimulatorConfigElement ele : config.getElements()) {



            if (SimulatorProperties.retrieveEndpoint.equals(ele.name)) {

                newRow();
                new ConfigTextDisplayBox(config.get(ele.name), tbl, getRow());
            } else if (SimulatorProperties.retrieveTlsEndpoint.equals(ele.name)) {

                newRow();
                new ConfigTextDisplayBox(config.get(ele.name), tbl, getRow());
            }


            // Can display more elements here

        }


    }

    public void displaySupplyState() {
        FlexTable tbl = getTbl();

        newRow();
        tbl.setWidget(getRow(), 0, new HTML("<hr/>"
                + "<h2>On-Demand Document Supply State</h2>"
                + "<p>This table displays the state of each On-Demand Document. If the Repository value is blank, it indicates that the Persistence Option did not apply for the On-Demand Document Entry at the time it was created. In other words, there is no persistence of a snapshot. The first number in the the Supply State column indicates the index of a content section in its content bundle. It is a zero-based index so a 0 value indicates initial state. Subsequent retrieves will progress through each content section in the content bundle and an option to Reset is then made available. To reset the content supply state, click the Reset link in the Supply State column.</p>" ));
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

        newRow();
//        tbl.setWidget(getRow(), 0, new HTML("Supply State"));

        oddeEntriesTbl.setBorderWidth(1);

        tbl.setWidget(getRow(), 0, oddeEntriesTbl);

        getOdDocumentEntries();
        addTable(tbl);
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

    }

    public void addTable(FlexTable tbl) {
        hpanel = new HorizontalPanel();

        panel.add(hpanel);
        hpanel.add(tbl);

//        hpanel.display(saveButton);
        hpanel.add(HtmlMarkup.html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        panel.add(HtmlMarkup.html("<br />"));

    }


    private void getOdDocumentEntries() {
        new GetOnDemandDocumentEntryDetailsCommand(){
            @Override
            public void onComplete(List<DocumentEntryDetail> documentEntryDetails) {
                prepareOddeTable(documentEntryDetails);
            }
        }.run(new GetOnDemandDocumentEntryDetailsRequest(ClientUtils.INSTANCE.getCommandContext(),getConfig().getId()));
    }

    private void prepareOddeTable(List<DocumentEntryDetail> documentEntryDetails) {
        oddeEntriesTbl.clear();
        int oddeRow = 0;

        oddeEntriesTbl.setWidget(oddeRow, 0, new HTML("<b>Created On</b>"));
        oddeEntriesTbl.setWidget(oddeRow, 1, new HTML("<b>On-Demand Document Unique ID</b>"));
        oddeEntriesTbl.setWidget(oddeRow, 2, new HTML("<b>Registry</b>"));
        oddeEntriesTbl.setWidget(oddeRow, 3, new HTML("<b>Repository</b>"));
        oddeEntriesTbl.setWidget(oddeRow, 4, new HTML("<b>Patient ID</b>"));

        HorizontalPanel ssHp = new HorizontalPanel();
        ssHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        ssHp.add(new HTML("<b>Supply State</b>"));
        ssHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        refreshImg.setAltText("Refresh");
        refreshImg.setTitle("Refresh");
        refreshImg.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        refreshImg.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);

        ssHp.add(refreshImg);

        oddeEntriesTbl.setWidget(oddeRow, 5, ssHp);
        oddeRow++;


        if (documentEntryDetails!=null) {
            // Sort descending (List is originally in ascending order)
            int oDdocCount = documentEntryDetails.size()-1 /* Z-B Idx*/;
            for (int cx=oDdocCount; cx>-1; cx--) {
                final DocumentEntryDetail ded = documentEntryDetails.get(cx);
                oddeEntriesTbl.setWidget(oddeRow, 0, new HTML(ded.getTimestamp()));
                oddeEntriesTbl.setWidget(oddeRow, 1, new HTML(ded.getUniqueId()));
                oddeEntriesTbl.setWidget(oddeRow, 2, new HTML(ded.getRegSiteSpec().getName()));
                oddeEntriesTbl.setWidget(oddeRow, 3, new HTML(ded.getReposSiteSpec()==null?"&nbsp;":ded.getReposSiteSpec().getName()));
                oddeEntriesTbl.setWidget(oddeRow, 4, new HTML(ded.getPatientId()));

                if (ded.getContentBundleSections()!=null && ded.getContentBundleSections().size()>0) {
                    String bundlePeek = "";
                    if (ded.getSupplyStateIndex()<=ded.getContentBundleSections().size()-1) {
                        int moreCt =  ded.getContentBundleSections().size() -  (ded.getSupplyStateIndex()+1);
                        if (moreCt>0)
                            bundlePeek = ", [" + moreCt + " more On-Demand document(s)]";
                        else
                            bundlePeek = ", no more On-Demand documents.";
                    }
                    final HorizontalPanel supplyStateHPanel =  new HorizontalPanel();
                    HTML supplyStateHtml = new HTML(""+ded.getSupplyStateIndex() + ": " + ded.getContentBundleSections().get(ded.getSupplyStateIndex()) + bundlePeek);
                    supplyStateHPanel.add(supplyStateHtml);

                    if (ded.getSupplyStateIndex()>0) {
                        supplyStateHPanel.add(new HTML("&nbsp;"));
                        Anchor resetLnk = new Anchor("Reset.");
                        resetLnk.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                new SetOdSupplyStateIndexCommand() {
                                    @Override
                                    public void onComplete(Boolean result) {
                                        if (result.booleanValue())  {
                                            getOdDocumentEntries();
                                        } else {
                                            new PopupMessage("Reset for document UniqueId ["+ded.getUniqueId()+"] failed!");
                                        }
                                    }
                                }.run(new SetOdSupplyStateIndexRequest(ClientUtils.INSTANCE.getCommandContext(),new SimId(config.getId().toString()),ded,0));
                            }
                        });
                        supplyStateHPanel.add(resetLnk);
                    }

                    oddeEntriesTbl.setWidget(oddeRow, 5, supplyStateHPanel);
                } else
                    oddeEntriesTbl.setWidget(oddeRow, 5, new HTML(""+ded.getSupplyStateIndex() + ": Missing Content Bundle!"));
                oddeRow++;
            }
        }
    }

    final HTML lblRegSiteBoxes = HtmlMarkup.html(SimulatorProperties.oddsRegistrySite);
    final VerticalPanel regOptsVPanel = new VerticalPanel();
    final VerticalPanel regActionVPanel = new VerticalPanel();

    private void popRegSites(final SimulatorConfigElement oddsRegistrySite) {
        if (oddsRegistrySite!=null) {
            new GetSiteNamesByTranTypeCommand() {
                @Override
                public void onComplete(List<String> results) {
                    regSiteBoxes.clear();
                    regActionVPanel.clear();
                    regOptsVPanel.clear();

                    regOptsVPanel.add(regSiteBoxes);
                    regSSP = new SiteSelectionPresenter("regSites", results, oddsRegistrySite.asList(), regSiteBoxes);
                    List<String> siteNames = regSSP.getSiteNames();
                    String errMsg = "";
                    if (siteNames == null || (siteNames != null && siteNames.size() == 0)) {
                        errMsg += "<li style='color:red'>No registry sites supporting an ODDE transaction are found/configured.</li>" +
                                "<li style='color:red'>Please display a Registry site using the Simulator Manager or configure a Site that supports an ODDE transaction.</li>";
                        regSiteBoxes.add(new HTML("<ul>" + errMsg + "</ul>"));
                    } else {
                        regButton.getElement().getStyle().setPaddingLeft(6, Style.Unit.PX);
                        regActionVPanel.add(regButton);

                    }
                }
            }.run(new GetSiteNamesByTranTypeRequest(ClientUtils.INSTANCE.getCommandContext(), TransactionType.REGISTER_ODDE.getName()));
        }
    }

    private void displayRegisterOptions() {
        SimulatorControlTab simulatorControlTab = getSimulatorControlTab();
        final SimulatorConfig config = getConfig();
        final FlexTable tbl = getTbl();


        newRow();
        tbl.setWidget(getRow(),0,new HTML("<hr/>"));
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

        newRow();
        FlexTable sectionHeaderTbl = new FlexTable();
        sectionHeaderTbl.setWidget(0,0,new HTML("<h2>First, Initialize this Simulator by Registering an On-Demand Document Entry</h2><p>Enter a Patient Id for the ODDE. You may use the Patient Identity Feed (PIF) tool to display a new patient Id. If persistence of On-Demand documents is desired, you must select the Persistence Option before clicking the Initialize button.</p>" ));
        Button saveButton = new Button("Save");
        saveButton.addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        // Only save, no validation takes place here.
                        saveSimConfig();
                    }
                }
        );
        sectionHeaderTbl.setWidget(0, 1, saveButton);
        sectionHeaderTbl.getFlexCellFormatter().setHorizontalAlignment(0,1, HasHorizontalAlignment.ALIGN_RIGHT);
        sectionHeaderTbl.getFlexCellFormatter().setVerticalAlignment(0,1, HasVerticalAlignment.ALIGN_MIDDLE);
        sectionHeaderTbl.setWidth("100%");
        tbl.setWidget(getRow(), 0, sectionHeaderTbl);
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);


        SimulatorConfigElement oddePatientId = config.get(SimulatorProperties.oddePatientId);
        if (oddePatientId!=null) {
            newRow();
            oddePatientIdCEBox.configure(oddePatientId,tbl,getRow());
            //                        oddePatientIdCEBox.setVisible(persistenceCb.getValue());
        }

        SimulatorConfigElement testplanToRegisterAndSupply =  config.get(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT);
//        if (testplanToRegisterAndSupply!=null) {
//            newRow();
//            testPlanCEBox.configure(testplanToRegisterAndSupply, tbl, getRow());
//                                    /* testPlanCEBox.setVisible(persistenceCb.getValue()); */
//        }


        contentBundleLbx.addItem(choose,"");
        contentBundleLbx.setSelectedIndex(0); // Make it default so the user should change it
        loadTestsFromCollection(contentBundleLbx, "ODContentBundle");
        newRow();
        tbl.setWidget(getRow(),0,new HTML(testplanToRegisterAndSupply.getName()));
        tbl.setWidget(getRow(),1, contentBundleLbx);


        SimulatorConfigElement repositoryUniqueId = config.get(SimulatorProperties.repositoryUniqueId);
        if (repositoryUniqueId!=null) {
            newRow();
            oddsReposTDBox = new ConfigTextDisplayBox(repositoryUniqueId, tbl, getRow());
//            oddsReposTDBox.configure(repositoryUniqueId, tbl, getRow());
//            oddsReposTDBox.getLblTextBox().setText("ODDS " + repositoryUniqueId.name);
        }

        // Persistence Option
        displayPersistenceConfig();

        final SimulatorConfigElement oddsRegistrySite = config.get(SimulatorProperties.oddsRegistrySite);

        if (oddsRegistrySite!=null) {
            newRow();
            tbl.setWidget(getRow(),0,lblRegSiteBoxes);
            tbl.setWidget(getRow(),1,regOptsVPanel);

            newRow();
            tbl.setWidget(getRow(),0,new HTML("&nbsp;"));
            tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

            newRow();
            tbl.setWidget(getRow(),0,new HTML("&nbsp;"));
            tbl.setWidget(getRow(),1,regActionVPanel);

            newRow();
            tbl.setWidget(getRow(),0, regActionMessage);
            tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

            popRegSites(oddsRegistrySite);

            addActionHandlers();
        }
    }

    private void addActionHandlers() {
        regButton.addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        if (validateParams()) {
                            setRegButton("Please wait...", false);
                            saveSimConfig();
                            registerODDE();
                        }
                    }
                }
        );
        refreshImg.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getOdDocumentEntries();
            }
        });
    }


    void loadTestsFromCollection(final ListBox lbx, final String testCollectionName) {
        new GetCollectionCommand() {
            @Override
            public void onComplete(Map<String, String> result) {
                Set<String> testNumsSet = result.keySet();
                List<String> testNums = new ArrayList<String>();
                testNums.addAll(testNumsSet);
                testNums = StringSort.sort(testNums);

                for (String name : testNums) {
                    String description = result.get(name);
                    lbx.addItem(name + " - " + description, name);
                }

                if (lbx.getItemCount() > 0) {
                    lbx.setSelectedIndex(0);
                }
            }
        }.run(new GetCollectionRequest(ClientUtils.INSTANCE.getCommandContext(), "collections", testCollectionName));
    }

    private void registerODDE() {

        if (regSSP !=null) {
            List<String> selectedReg = regSSP.getSelected();
            if ((selectedReg != null && selectedReg.size() == 0)) {
                new PopupMessage("Please select a Registry.");
                return;
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("$patientid$", oddePatientIdCEBox.getTb().getValue());
        params.put("$repuid$", getConfig().get(SimulatorProperties.repositoryUniqueId).asString()); // oddsReposTDBox.toString() getTb().getValue());

        new RegisterWithLocalizedTrackingInODDSCommand(){
            @Override
            public void onFailure(Throwable throwable) {
                regActionMessage.getElement().getStyle().setColor("red");
                regActionMessage.setText("Error: " + throwable.getMessage());

                setRegButton("Initialize", true);
            }
            @Override
            public void onComplete(Map<String, String> responseMap) {
                setRegButton("Initialize", true);
                if (responseMap.containsKey("error")) {
                    regActionMessage.getElement().getStyle().setColor("red");

                    StringBuffer sb = new StringBuffer();
                    sb.append(responseMap.get("error"));
                    sb.append("<br/>");

                    for (int cx=0; cx < responseMap.size()-1; cx++) {
                        sb.append(responseMap.get("assertion"+cx));
                        sb.append("<br/>");
                    }
                    regActionMessage.setHTML(sb.toString());
                } else {
                    regActionMessage.getElement().getStyle().setColor("black");
                    regActionMessage.setHTML("<br/>Registration was successful. ODDE Id is " + responseMap.get("key"));
                    getOdDocumentEntries();
                }
            }
        }.run(new RegisterRequest(ClientUtils.INSTANCE.getCommandContext(),
                getConfig().getId().getUser(),new TestInstance(contentBundleLbx.getSelectedValue()),
                new SiteSpec(regSSP.getSelected().get(0), ActorType.REGISTRY, null),params, getConfig().getId()));
    }

    private void setRegButton(String initialize, boolean enabled) {
        regButton.setText(initialize);
        regButton.setEnabled(enabled);
    }

    /**
     *
     */
    private boolean validateParams() {
        String errMsg = "";

        if ("".equals(oddePatientIdCEBox.getTb().getValue())) {
            errMsg += "<li>An On-Demand Document Entry Patient ID is required.</li>";
        }
        if ("".equals(contentBundleLbx.getSelectedValue())) {
            errMsg += "<li>A testplan number to Register an On-Demand Document Entry and to Supply Content is required.</li>";
        }
        /*
        if ("".equals(oddsReposTDBox.getTb().getValue())) {
            errMsg += "<li>An ODDS repository Id is required.</li>";
        }*/


        // If the persistence option is ON, then saving should activate the register OD transaction.
        if (persistenceCb.getValue()) {
            if (reposSSP !=null) {
                List<String> selectedRepos = reposSSP.getSelected();
                List<String> siteNames = reposSSP.getSiteNames();
                if (selectedRepos==null || (selectedRepos!=null && selectedRepos.size()==0)) {
                    errMsg += "<li>The persistence option requires a repository but none are selected. Please select a repository.</li>";
                } else if (siteNames==null || (siteNames!=null && siteNames.size()==0)) {
                    errMsg += "<li>Persistence option requires a repository but none are found/configured. Please display a Repository using the Simulator Manager or configure a Site that supports a PnR transaction.</li>";
                }
            } else {
                errMsg += "<li>siteSelectionPresenter is null!</li>";
            }

        }

        if (!"".equals(errMsg)) {
            SafeHtmlBuilder errMsgHtml = new SafeHtmlBuilder();
            errMsgHtml.appendHtmlConstant("<h3>Error(s):</h3>"); //


            new PopupMessage(errMsgHtml.toSafeHtml(), new HTML("<ul>" +  errMsg + "</ul>"));
            return false;
        }

        return true;
    }



    public void saveSimConfig() {
        getConfig().get(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS).setBooleanValue(persistenceCb.getValue());
        getConfig().get(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT).setStringValue(contentBundleLbx.getSelectedValue());
        getConfig().get(SimulatorProperties.oddsRepositorySite).setStringListValue(reposSSP.getSelected());
        getConfig().get(SimulatorProperties.oddsRegistrySite).setStringListValue(regSSP.getSelected());

        new PutSimConfigCommand(){
            @Override
            public void onComplete(String result) {
                // reload simulators to getRetrievedDocumentsModel updates
                new LoadSimulatorsClickHandler(simulatorControlTab, testSession).onClick(null);
            }
        }.run(new SimConfigRequest(ClientUtils.INSTANCE.getCommandContext(),config));
    }

    public int getRow() {
        return row;
    }

    public int newRow() {
        return ++row;
    }


    public SimulatorConfig getConfig() {
        return config;
    }

    public FlexTable getTbl() {
        return tbl;
    }

    public SimulatorControlTab getSimulatorControlTab() {
        return simulatorControlTab;
    }

    public FlowPanel getPanel() {
        return panel;
    }
}
