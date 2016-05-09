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
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.BaseSimConfigMgr;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.ConfigEditBox;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SimulatorControlTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SiteSelectionPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Selecting some simulator configuration elements require the feature of additional input elements. This class overlays event-driven UI elements specified here over the basic-style elements created by the base config mgr
 * Created by skb1 on 3/24/2016.
 */
public class OddsSimConfigMgr extends BaseSimConfigMgr {
    CheckBox persistenceCb = new CheckBox();
    HorizontalPanel reposSiteBoxes = new HorizontalPanel();
    SiteSelectionPresenter reposSSP;
    HorizontalPanel regSiteBoxes = new HorizontalPanel();
    SiteSelectionPresenter regSSP;
    ConfigEditBox oddePatientIdCEBox = new ConfigEditBox();
    ConfigEditBox testPlanCEBox = new ConfigEditBox();
    int headerRow = 0;
    Map<String, String> oddeMap = new HashMap<String, String>();


    public OddsSimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config, String testSession) {
        super(simulatorControlTab, panel, config, testSession);

        // TODO: Populate the ODDE map here.
        // Create a server side method to retrieve all oddes and supply state idx as a map in the ODDS repository.
    }

    @Override
    public void displayHeader() {
        getPanel().add(new HTML("<h1>On-Demand Document Source (ODDS) Simulator Configuration</h1>"));

        getPanel().add(new HTML("<p>" +
                 "This simulator supports testing of Registration and Retrieval of On-Demand patient documents." +
                 "</p>" +
                "<hr/>" +

                "<h2>Retrieving an On-Demand Document</h2>" +
                "<p>A Document Consumer may Retrieve an On-Demand Document from an ODDS using its ODDS Repository ID. If the Persistence Option is enabled, the selected Repository must be configured to forward registry requests to the same Registry holding the On-Demand Document Entry." +
                "</p>" +
                "<hr/>" +

                "<h2>Simulator Configuration</h2>" +
                "<p></p>"


        ));
    }

    @Override
    public void displayBasicSimulatorConfig() {
        super.displayBasicSimulatorConfig();
        headerRow = getRow();
    }

    @Override
    public void displayInPanel() {
        super.displayInPanel();

        SimulatorControlTab simulatorControlTab = getSimulatorControlTab();
        int row = headerRow;
        SimulatorConfig config = getConfig();
        FlexTable tbl = getTbl();
        final HorizontalPanel oddsRepositorySitePanel = new HorizontalPanel();
        final HTML lblReposSiteBoxes = HtmlMarkup.html(SimulatorProperties.oddsRepositorySite);



        // At this point the base class will have already created all basic elements
        // Render only the interesting config elements and then overlay them over the basic widget based on the row number
        for (final SimulatorConfigElement ele : config.getElements()) {

            // String
            if (ele.isString()) {
                if (ele.isEditable()) {

                    if (SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT.equals(ele.name)) {
                        testPlanCEBox.configure(ele, tbl, row);
//                        testPlanCEBox.setVisible(persistenceCb.getValue());
                    } else if (SimulatorProperties.oddePatientId.equals(ele.name)) {
                        oddePatientIdCEBox.configure(ele,tbl,row);
//                        oddePatientIdCEBox.setVisible(persistenceCb.getValue());
                    } else if (SimulatorProperties.repositoryUniqueId.equals(ele.name)) {
                        ConfigEditBox oddsReposCeb = new ConfigEditBox(ele, tbl, row);
                        oddsReposCeb.getLblTextBox().setText("ODDS " + ele.name);
                    }
                } else {
                    if (SimulatorProperties.oddsContentSupplyState.equals(ele.name)) {

                        tbl.setWidget(row, 0, new HTML(ele.name));
                        if (!oddeMap.isEmpty()) {
                            FlexTable oddeEntries = new FlexTable();
                            oddeEntries.setBorderWidth(1);
                            int oddeRow = 0;
                            oddeEntries.setWidget(oddeRow, 0, new HTML("ODDE ID"));
                            oddeEntries.setWidget(oddeRow++, 1, new HTML("Supply State Index"));
                            for (String key : oddeMap.keySet()) {
                                oddeEntries.setWidget(oddeRow, 0, new HTML(key));
                                oddeEntries.setWidget(oddeRow++, 1, new HTML(oddeMap.get(key)));
                            }

                            tbl.setWidget(row, 1, oddeEntries);
                        } else {
                            tbl.setWidget(row, 1, new HTML("&nbsp;"));
                        }

                    }
                }
                row++;
            }

            // Boolean
            else if (ele.isBoolean()) {

                // Need to group other related controls based on this selector
                if (SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS.equals(ele.name)) {
                    tbl.setText(row, 0, ele.name.replace('_', ' '));

                    persistenceCb.setValue(ele.asBoolean());
                    persistenceCb.setEnabled(ele.isEditable());
                    tbl.setWidget(row, 1, persistenceCb);
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

                row++;
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

                tbl.setWidget(row, 0, lblReposSiteBoxes);
                tbl.setWidget(row, 1, reposSiteBoxes);

                getSaveButton().addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                ele.setValue(reposSSP.getSelected());
                            }
                        }
                );
                row++;

                boolean persistenceOpt = persistenceCb.getValue();
                lblReposSiteBoxes.setVisible(persistenceOpt);
                reposSiteBoxes.setVisible(persistenceOpt);
            }

            else if (SimulatorProperties.oddsRegistrySite.equals(ele.name)) {
                final HTML lblRegSiteBoxes = HtmlMarkup.html(SimulatorProperties.oddsRegistrySite);

                // Title and description
                final FlexTable nestedTbl = new FlexTable();
                nestedTbl.setWidget(0,0,new HTML("<h2>Registering an On-Demand Document Entry</h2>" +
                "<p><span style='color:red'>Please note:</span>An On-Demand Document Entry for a patient must be registered before this simulator can be used.</p>"));
                nestedTbl.getFlexCellFormatter().setColSpan(0,0,2);

                // Action


                final HorizontalPanel horizontalPanel = new HorizontalPanel();
                nestedTbl.setWidget(1, 0, horizontalPanel);
                nestedTbl.getFlexCellFormatter().setColSpan(1,0,2);


                horizontalPanel.add(lblRegSiteBoxes);

                simulatorControlTab.toolkitService.getSiteNamesByTranType(TransactionType.REGISTER_ODDE.getName(), new AsyncCallback<List<String>>() {

                    public void onFailure(Throwable caught) {
                        new PopupMessage("getSiteNamesByTranType REGISTER_ODDE:" + caught.getMessage());
                    }

                    public void onSuccess(List<String> results) {
                        regSSP = new SiteSelectionPresenter(results, ele.asList(), regSiteBoxes);
                        horizontalPanel.add(regSiteBoxes);

                        List<String> siteNames = regSSP.getSiteNames();
                        String errMsg = "";

                        if (siteNames==null || (siteNames!=null && siteNames.size()==0)) {

                            errMsg += "<li style='color:red'>No registry sites supporting an ODDE transaction are found/configured.</li>"+
                                    "<li style='color:red'>Please add a Registry site using the Simulator Manager or configure a Site that supports an ODDE transaction.</li>";

                            horizontalPanel.add(new HTML("<ul>" +  errMsg + "</ul>"));

                        } else {
                            Button regButton = new Button("Register an On-Demand Document Entry");
                            regButton.getElement().getStyle().setPaddingLeft(6, Style.Unit.PX);
//                            verticalPanel.getElement().getStyle().setMarginBottom(4, Style.Unit.PX);
//                            regButton.getElement().getStyle().setMarginTop(4, Style.Unit.PX);

                            nestedTbl.setWidget(2,0,regButton);
                            nestedTbl.getFlexCellFormatter().setColSpan(3,0,2);

                            regButton.addClickHandler(
                                    new ClickHandler() {
                                        @Override
                                        public void onClick(ClickEvent clickEvent) {
                                            ele.setValue(regSSP.getSelected());
                                            if (validateParams()) {
                                                saveSimConfig();
                                                registerODDE();
                                            }
                                        }
                                    }
                            );

                        }

                    }
                });

                tbl.setWidget(row, 0, nestedTbl);
                tbl.getFlexCellFormatter().setColSpan(row,0,2);

                row++;
            }

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


    private void registerODDE() {

        if (regSSP !=null) {
            List<String> selectedRepos = reposSSP.getSelected();
            if ((selectedRepos != null && selectedRepos.size() == 0)) {
                new PopupMessage("Please select a Registry.");
                return;
            }
        }

        getSimulatorControlTab().toolkitService.registerWithLocalizedTrackingInODDS(getConfig().getId().getUser(), new TestInstance(testPlanCEBox.getTb().getValue())
                , oddePatientIdCEBox.getTb().getValue(), new SiteSpec(regSSP.getSelected().get(0)), new SiteSpec(getConfig().getId().toString())
                , new AsyncCallback<Map<String, String>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        new PopupMessage("Sorry, registration of an ODDE failed: " + throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Map<String, String> uid_SupplyIdx) {

                        for (String key : uid_SupplyIdx.keySet()) {
                            new PopupMessage(uid_SupplyIdx.get(key));
                        }


                    }
                });




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
}
