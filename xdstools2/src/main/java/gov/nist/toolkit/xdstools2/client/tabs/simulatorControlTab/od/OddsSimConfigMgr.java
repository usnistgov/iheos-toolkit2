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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
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
    ConfigEditBox oddsReposCEBox = new ConfigEditBox();
    Map<String, String> oddeMap = new HashMap<String, String>();
    Button regButton = new Button("Initialize"); // Register an On-Demand Document Entry
    HTML regActionErrors = new HTML();
    int row = 0;

    public OddsSimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config, String testSession) {

        this.simulatorControlTab = simulatorControlTab;
        this.panel = panel;
        this.config = config;
        this.testSession = testSession;


        // TODO: Populate the ODDE map here.
        // Create a server side method to retrieve all oddes and supply state idx as a map in the ODDS repository.
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
                 "This simulator supports testing of Registration and Retrieval of On-Demand patient documents." +
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
        tbl.setWidget(getRow(), 0, new HTML("<hr/>" + "<h2>Simulator Configuration</h2>"));
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
        new ConfigEditBox(config.get("Name") , tbl, getRow());

        newRow();
        new ConfigBooleanBox(config.get(SimulatorProperties.FORCE_FAULT), tbl, getRow());

        newRow();
        new ConfigTextDisplayBox(config.get(SimulatorProperties.environment),tbl, getRow());

    }

    @Override
    public void displayInPanel() {


        // Register config
        displayRegisterOptions();

        // Basic Simulator config
        displayBasicSimulatorConfig();


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
                                reposSSP = new SiteSelectionPresenter(results, ele.asList(), reposSiteBoxes);
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

            } else if (SimulatorProperties.retrieveEndpoint.equals(ele.name)) {

                newRow();
                new ConfigTextDisplayBox(config.get(ele.name), tbl, getRow());
            } else if (SimulatorProperties.retrieveTlsEndpoint.equals(ele.name)) {

                newRow();
                new ConfigTextDisplayBox(config.get(ele.name), tbl, getRow());
            }


            // Can add more elements here

        }

        // TODO:
        // FIXME: Remove SCE for supply state use, values should be coming from an ODDS rep index
        // May need a server side call to retrieve a map containing values
        SimulatorConfigElement oddsContentSupplyState = config.get(SimulatorProperties.oddsContentSupplyState);
        if (oddsContentSupplyState != null) {

            newRow();
            tbl.setWidget(getRow(), 0, new HTML(oddsContentSupplyState.name));
            FlexTable oddeEntries = new FlexTable();
            oddeEntries.setBorderWidth(1);
            int oddeRow = 0;
            oddeEntries.setWidget(oddeRow, 0, new HTML("On-Demand Document Entry ID"));
            oddeEntries.setWidget(oddeRow, 1, new HTML("Supply State Index"));
            oddeRow++;
            if (!oddeMap.isEmpty()) {
                for (String key : oddeMap.keySet()) {
                    oddeEntries.setWidget(oddeRow, 0, new HTML(key));
                    oddeEntries.setWidget(oddeRow, 1, new HTML(oddeMap.get(key)));
                    oddeRow++;
                }
            }
            tbl.setWidget(getRow(), 1, oddeEntries);

        }

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

    private void displayRegisterOptions() {
        SimulatorControlTab simulatorControlTab = getSimulatorControlTab();
        final SimulatorConfig config = getConfig();
        final FlexTable tbl = getTbl();

        newRow();
        tbl.setWidget(getRow(), 0, new HTML("<h2>Registering an On-Demand Document Entry</h2>" +
                "<p><span style='color:black'>Note: </span>An On-Demand Document Entry for a patient must be registered before this simulator can be used.</p>"));
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
            oddsReposCEBox.configure(repositoryUniqueId, tbl, getRow());
            oddsReposCEBox.getLblTextBox().setText("ODDS " + repositoryUniqueId.name);
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
            tbl.setWidget(getRow(),0,regActionErrors);
            tbl.getFlexCellFormatter().setColSpan(getRow(),0,2);


            simulatorControlTab.toolkitService.getSiteNamesByTranType(TransactionType.REGISTER_ODDE.getName(), new AsyncCallback<List<String>>() {

                public void onFailure(Throwable caught) {
                    new PopupMessage("getSiteNamesByTranType REGISTER_ODDE:" + caught.getMessage());
                }

                public void onSuccess(List<String> results) {


                    regSSP = new SiteSelectionPresenter(results, oddsRegistrySite.asList(), regSiteBoxes);
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

                        regButton.addClickHandler(
                                new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent clickEvent) {
                                        oddsRegistrySite.setValue(regSSP.getSelected());
                                        if (validateParams()) {
                                            setRegButton("Please wait...", false);
                                            saveSimConfig();
                                            registerODDE();
                                        }
                                    }
                                }
                        );

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

        hpanel.add(saveButton);
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
        params.put("$repuid$", oddsReposCEBox.getTb().getValue());

        getSimulatorControlTab().toolkitService.registerWithLocalizedTrackingInODDS(getConfig().getId().getUser(), new TestInstance(testPlanCEBox.getTb().getValue())
                , new SiteSpec(regSSP.getSelected().get(0)), getConfig().getId() , params
                , new AsyncCallback<Map<String, String>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        regActionErrors.getElement().getStyle().setColor("red");
                        regActionErrors.setText("Error: " + throwable.getMessage());

                        setRegButton("Initialize", true);
                    }

                    @Override
                    public void onSuccess(Map<String, String> responseMap) {

                        setRegButton("Initialize", true);
                        if (responseMap.containsKey("error")) {
//                            new PopupMessage("Sorry, registration of an On-Demand Document Entry failed: ");
                            regActionErrors.getElement().getStyle().setColor("red");

                            StringBuffer sb = new StringBuffer();
                            sb.append(responseMap.get("error"));
                            sb.append("<br/>");

                            for (int cx=0; cx < responseMap.size()-1; cx++) {
                                sb.append(responseMap.get("assertion"+cx));
                                sb.append("<br/>");
                            }
                            regActionErrors.setHTML(sb.toString());
                        } else {
                            regActionErrors.getElement().getStyle().setColor("black");
                            regActionErrors.setText("Registration was successful. ODDE Id is " + responseMap.get("key"));

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
        if ("".equals(oddsReposCEBox.getTb().getValue())) {
            errMsg += "<li>An ODDS repository Id is required.</li>";
        }


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
