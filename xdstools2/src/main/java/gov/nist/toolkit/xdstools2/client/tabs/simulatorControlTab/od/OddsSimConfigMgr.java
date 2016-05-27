package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.ConfigBooleanBox;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.ConfigEditBox;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.ConfigTextDisplayBox;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.LoadSimulatorsClickHandler;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SimulatorControlTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SiteSelectionPresenter;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.intf.SimConfigMgrIntf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by skb1 on 3/24/2016.
 */
public class OddsSimConfigMgr implements SimConfigMgrIntf {

    private SimulatorControlTab simulatorControlTab;
    VerticalPanel panel;
    HorizontalPanel hpanel;
    SimulatorConfig config;
    String testSession;
    FlexTable tbl = new FlexTable();
    Button saveButton = new Button("Save");

    CheckBox persistenceCb = new CheckBox();
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
    Button refreshSupplyState = new Button("Refresh");

    public OddsSimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config, String testSession) {

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
                 "This simulator supports testing of Registration and Retrieval of On-Demand patient documents. First, initialize this simulator by registering an On-Demand Document Entry and selecting the Persistence option in the Retrieve Configuration section. Next, retrieve the document." +
                 "" +
                "<hr/>"
//                +
//
//                "<h2>Retrieving an On-Demand Document</h2>" +
//                "<p>A Document Consumer may Retrieve an On-Demand Document from an ODDS using its ODDS Repository ID. If the Persistence Option is enabled, the selected Repository must be configured to forward registry requests to the same Registry holding the On-Demand Document Entry." +
//                "</p>" +
//                "<hr/>" +
//
//                "<h2>Simulator Configuration</h2>" +
//                "<p></p>"


        ));
    }

    @Override
    public void displayBasicSimulatorConfig() {
        FlexTable tbl = getTbl();

        newRow();
        tbl.setWidget(getRow(), 0, new HTML("<h2>Simulator Configuration</h2>"));
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);


        newRow();
        tbl.setWidget(getRow(), 0, HtmlMarkup.html("Simulator Type"));
        tbl.setWidget(getRow(), 1, HtmlMarkup.html(config.getActorTypeFullName()));

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
        new ConfigEditBox(config.get(SimulatorProperties.codesEnvironment),tbl, getRow());

    }

    @Override
    public void displayInPanel() {


        // Basic Simulator config
        displayBasicSimulatorConfig();

        // Register config
        displayRegisterOptions();

        // Retrieve config
        displayRetrieveConfig();

    }

    public void displayPersistenceConfig() {

    }

    public void displayRetrieveConfig() {
        SimulatorControlTab simulatorControlTab = getSimulatorControlTab();
        final SimulatorConfig config = getConfig();
        FlexTable tbl = getTbl();
        final HorizontalPanel oddsRepositorySitePanel = new HorizontalPanel();
        final HTML lblReposSiteBoxes = HtmlMarkup.html(SimulatorProperties.oddsRepositorySite);

        // Retrieve config
        newRow();
        tbl.setWidget(getRow(), 0, new HTML("<hr/>"
                + "<h2>Retrieve Configuration</h2>"
                + "<p>A Document Consumer may Retrieve an On-Demand Document from an ODDS using its ODDS Repository ID. If the Persistence Option is enabled, the selected Repository must be configured to forward registry requests to the same Registry holding the On-Demand Document Entry." ));
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);


        for (final SimulatorConfigElement ele : config.getElements()) {

            // Boolean
            if (ele.isBoolean()) {

                // Need to group other related controls based on this selector
                if (SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS.equals(ele.name)) {
                    newRow();

                    tbl.setText(getRow(), 0, ele.name.replace('_', ' '));

                    persistenceCb.setValue(ele.asBoolean());
                    persistenceCb.setEnabled(ele.isEditable());
                    tbl.setWidget(getRow(), 1, persistenceCb);
                    persistenceCb.addClickHandler(
                            new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    ele.setValue(persistenceCb.getValue());

                                    boolean persistenceOpt = persistenceCb.getValue();
                                    lblReposSiteBoxes.setVisible(persistenceOpt);
                                    reposSiteBoxes.setVisible(persistenceOpt);
//                                    oddePatientIdCEBox.setVisible(persistenceOpt);
//                                    testPlanCEBox.setVisible(persistenceOpt);

                                }
                            }
                    );
                }


            }

            // Selecting a Repository for the ODDS
            else if (SimulatorProperties.oddsRepositorySite.equals(ele.name)) {
                simulatorControlTab.toolkitService.getSiteNamesByTranType(TransactionType.PROVIDE_AND_REGISTER.getName(), new AsyncCallback<List<String>>() {

                    public void onFailure(Throwable caught) {
                        new PopupMessage("getSiteNamesByTranType PROVIDE_AND_REGISTER:" + caught.getMessage());
                    }

                    public void onSuccess(List<String> results) {
                        reposSSP = new SiteSelectionPresenter("reposSites", results, ele.asList(), reposSiteBoxes);
                    }
                });

                // ----

                newRow();
                tbl.setWidget(getRow(), 0, lblReposSiteBoxes);
                tbl.setWidget(getRow(), 1, reposSiteBoxes);

                getSaveButton().addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                ele.setValue(reposSSP.getSelected());
                            }
                        }
                );

                boolean persistenceOpt = persistenceCb.getValue();
                lblReposSiteBoxes.setVisible(persistenceOpt);
                reposSiteBoxes.setVisible(persistenceOpt);

            }
           if (SimulatorProperties.retrieveEndpoint.equals(ele.name)) {

                newRow();
                new ConfigTextDisplayBox(config.get(ele.name), tbl, getRow());
            } else if (SimulatorProperties.retrieveTlsEndpoint.equals(ele.name)) {

                newRow();
                new ConfigTextDisplayBox(config.get(ele.name), tbl, getRow());
            }


            // Can add more elements here

        }


        newRow();
        tbl.setWidget(getRow(), 0, new HTML("On-Demand Document Supply State"));

        oddeEntriesTbl.setBorderWidth(1);

        tbl.setWidget(getRow(), 1, oddeEntriesTbl);

        getOdDocumentEntries(simulatorControlTab);
        addTable(tbl);

        getSaveButton().addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        if (validateParams()) {
                            saveSimConfig();
                        }
                    }
                }
        );
    }

    private void getOdDocumentEntries(SimulatorControlTab simulatorControlTab) {
        simulatorControlTab.toolkitService.getOnDemandDocumentEntryDetails(getConfig().getId(), new AsyncCallback<List<DocumentEntryDetail>>() {
            @Override
            public void onFailure(Throwable throwable) {
                regActionMessage.getElement().getStyle().setColor("red");
                regActionMessage.setText("getOnDemandDocumentEntryDetails Error:" + throwable.toString());
            }

            @Override
            public void onSuccess(List<DocumentEntryDetail> documentEntryDetails) {
                oddeEntriesTbl.clear();
                int oddeRow = 0;

//                refreshSupplyState.getElement().getStyle().setMarginLeft(80, Style.Unit.PCT);

                oddeEntriesTbl.setWidget(oddeRow, 0,refreshSupplyState);
                oddeEntriesTbl.getFlexCellFormatter().setColSpan(oddeRow,0,6);
                oddeEntriesTbl.getFlexCellFormatter().setHorizontalAlignment(oddeRow,0, HasHorizontalAlignment.ALIGN_RIGHT);
                oddeRow++;

                oddeEntriesTbl.setWidget(oddeRow, 0, new HTML("<b>Created On</b>"));
                oddeEntriesTbl.setWidget(oddeRow, 1, new HTML("<b>On-Demand Document Unique ID</b>"));
                oddeEntriesTbl.setWidget(oddeRow, 2, new HTML("<b>Registry</b>"));
                oddeEntriesTbl.setWidget(oddeRow, 3, new HTML("<b>Repository</b>"));
                oddeEntriesTbl.setWidget(oddeRow, 4, new HTML("<b>Patient ID</b>"));
                oddeEntriesTbl.setWidget(oddeRow, 5, new HTML("<b>Supply State</b>"));
                oddeRow++;

                if (documentEntryDetails!=null) {
                    // Sort descending (List is originally in ascending order)
//                  Collections.reverse(documentEntryDetails);   -- This seems to need an additional inherits module:
                    int oDdocCount = documentEntryDetails.size()-1 /* Z-B Idx*/;
                    for (int cx=oDdocCount; cx>-1; cx--) {
                        DocumentEntryDetail ded = documentEntryDetails.get(cx);
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
                            oddeEntriesTbl.setWidget(oddeRow, 5, new HTML(""+ded.getSupplyStateIndex() + ": " + ded.getContentBundleSections().get(ded.getSupplyStateIndex()) + bundlePeek ));
                        } else
                            oddeEntriesTbl.setWidget(oddeRow, 5, new HTML(""+ded.getSupplyStateIndex() + ": Missing Content Bundle!"));
                        oddeRow++;
                    }
                }
            }
        });
    }

    private void displayRegisterOptions() {
        SimulatorControlTab simulatorControlTab = getSimulatorControlTab();
        final SimulatorConfig config = getConfig();
        final FlexTable tbl = getTbl();

        newRow();
        tbl.setWidget(getRow(), 0, new HTML("<hr/> <h2>First, Initialize this Simulator by Registering an On-Demand Document Entry</h2>" +
                "<p>Enter a Patient Id and select a Registry site to register an On-Demand Document Entry. You may use the Patient Identity Feed (PIF) tool to add a new patient Id. If persistence of On-Demand Documents is desired, you must select the Persistence option from the Retrieve configuration prior to the registration. Click the Initialize button to register and setup a persistence indicator for retrieve requests.</p>"));
        tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);


        SimulatorConfigElement oddePatientId = config.get(SimulatorProperties.oddePatientId);
        if (oddePatientId!=null) {
            newRow();
            oddePatientIdCEBox.configure(oddePatientId,tbl,getRow());
            //                        oddePatientIdCEBox.setVisible(persistenceCb.getValue());
        }

        SimulatorConfigElement testplanToRegisterAndSupply =  config.get(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT);
        if (testplanToRegisterAndSupply!=null) {
            newRow();
            testPlanCEBox.configure(testplanToRegisterAndSupply, tbl, getRow());
            //                        testPlanCEBox.setVisible(persistenceCb.getValue());
        }

        SimulatorConfigElement repositoryUniqueId = config.get(SimulatorProperties.repositoryUniqueId);
        if (repositoryUniqueId!=null) {
            newRow();
            oddsReposTDBox = new ConfigTextDisplayBox(repositoryUniqueId, tbl, getRow());
//            oddsReposTDBox.configure(repositoryUniqueId, tbl, getRow());
//            oddsReposTDBox.getLblTextBox().setText("ODDS " + repositoryUniqueId.name);
        }


        final SimulatorConfigElement oddsRegistrySite = config.get(SimulatorProperties.oddsRegistrySite);

        if (oddsRegistrySite!=null) {
            final HTML lblRegSiteBoxes = HtmlMarkup.html(SimulatorProperties.oddsRegistrySite);


            final VerticalPanel regOptsVPanel = new VerticalPanel();
            final VerticalPanel regActionVPanel = new VerticalPanel();

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


            simulatorControlTab.toolkitService.getSiteNamesByTranType(TransactionType.REGISTER_ODDE.getName(), new AsyncCallback<List<String>>() {

                public void onFailure(Throwable caught) {
                    new PopupMessage("getSiteNamesByTranType REGISTER_ODDE:" + caught.getMessage());
                }

                public void onSuccess(List<String> results) {


                    regSSP = new SiteSelectionPresenter("regSites", results, oddsRegistrySite.asList(), regSiteBoxes);
                    regOptsVPanel.add(regSiteBoxes);

                    List<String> siteNames = regSSP.getSiteNames();
                    String errMsg = "";

                    if (siteNames==null || (siteNames!=null && siteNames.size()==0)) {

                        errMsg += "<li style='color:red'>No registry sites supporting an ODDE transaction are found/configured.</li>"+
                                "<li style='color:red'>Please add a Registry site using the Simulator Manager or configure a Site that supports an ODDE transaction.</li>";

                        regSiteBoxes.add(new HTML("<ul>" +  errMsg + "</ul>"));

                    } else {
                        regButton.getElement().getStyle().setPaddingLeft(6, Style.Unit.PX);
//                            verticalPanel.getElement().getStyle().setMarginBottom(4, Style.Unit.PX);
//                            regButton.getElement().getStyle().setMarginTop(4, Style.Unit.PX);


                        regActionVPanel.add(regButton);

                        getSaveButton().addClickHandler(
                                new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent clickEvent) {
                                        oddsRegistrySite.setValue(reposSSP.getSelected());
                                    }
                                }
                        );

                        regButton.addClickHandler(
                                new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent clickEvent) {
                                        getConfig().get(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS).setValue(persistenceCb.getValue());
                                        getConfig().get(SimulatorProperties.oddsRepositorySite).setValue(reposSSP.getSelected());
                                        getConfig().get(SimulatorProperties.oddsRegistrySite).setValue(regSSP.getSelected());
                                        oddsRegistrySite.setValue(regSSP.getSelected());
                                        if (validateParams()) {
                                            setRegButton("Please wait...", false);
                                            saveSimConfig();
                                            registerODDE();
                                        }
                                    }
                                }
                        );

                        refreshSupplyState.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                getOdDocumentEntries(getSimulatorControlTab());
                            }
                        });

                    }

                }
            });

//            newRow();
//            tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);

        }
    }

    public void addTable(FlexTable tbl) {
        hpanel = new HorizontalPanel();

        panel.add(hpanel);
        hpanel.add(tbl);

        FlexTable saveBarTbl = new FlexTable();
        saveBarTbl.setWidget(0,0,saveButton);
        saveBarTbl.getFlexCellFormatter().setVerticalAlignment(0,0, HasVerticalAlignment.ALIGN_TOP);
//        saveBarTbl.setWidget(1,0,saveButton);
//        saveBarTbl.getFlexCellFormatter().setVerticalAlignment(0,0, HasVerticalAlignment.ALIGN_BOTTOM);


        hpanel.add(saveBarTbl);
        hpanel.add(HtmlMarkup.html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        panel.add(HtmlMarkup.html("<br />"));

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

        getSimulatorControlTab().toolkitService.registerWithLocalizedTrackingInODDS(getConfig().getId().getUser(), new TestInstance(testPlanCEBox.getTb().getValue())
                , new SiteSpec(regSSP.getSelected().get(0), ActorType.REGISTRY, null), getConfig().getId() , params
                , new AsyncCallback<Map<String, String>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        regActionMessage.getElement().getStyle().setColor("red");
                        regActionMessage.setText("Error: " + throwable.getMessage());

                        setRegButton("Initialize", true);
                    }

                    @Override
                    public void onSuccess(Map<String, String> responseMap) {

                        setRegButton("Initialize", true);
                        if (responseMap.containsKey("error")) {
//                            new PopupMessage("Sorry, registration of an On-Demand Document Entry failed: ");
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
                            getOdDocumentEntries(getSimulatorControlTab());
                        }


                    }
                });




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
        if ("".equals(testPlanCEBox.getTb().getValue())) {
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
                    errMsg += "<li>Persistence option requires a repository but none are found/configured. Please add a Repository using the Simulator Manager or configure a Site that supports a PnR transaction.</li>";
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

    public void addSaveHandler() {
        // this is added last so other internal saves (above) happen first
        saveButton.addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        saveSimConfig();
                    }
                }
        );
    }

    public void saveSimConfig() {
        simulatorControlTab.toolkitService.putSimConfig(config, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("saveSimConfig:" + caught.getMessage());
            }

            public void onSuccess(String result) {
                // reload simulators to getRetrievedDocumentsModel updates
                new LoadSimulatorsClickHandler(simulatorControlTab, testSession).onClick(null);
            }
        });
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

    public Button getSaveButton() {
        return saveButton;
    }

    public VerticalPanel getPanel() {
        return panel;
    }
}
