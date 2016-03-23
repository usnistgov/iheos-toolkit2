package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.SimulatorProperties;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.intf.SimConfigMgrIntf;

import java.util.List;


/**
 * Manages the content of a single Simulator on the screen
 * @author bill
 * Add On-Demand configuration
 * @author sunil.bhaskarla
 *
 */
class OddsSimConfigMgr implements SimConfigMgrIntf {
    /**
     *
     */
    private SimulatorControlTab simulatorControlTab;
    VerticalPanel panel;
    HorizontalPanel hpanel;
    SimulatorConfig config;
    String testSession;
    FlexTable tbl = new FlexTable();
    Button saveButton = new Button("Save");

    CheckBox persistenceCb = new CheckBox();
    HorizontalPanel siteBoxes = new HorizontalPanel();
    SiteSelectionPresenter siteSelectionPresenter;
    ConfigEditBox oddePatientIdCEBox = new ConfigEditBox();
    ConfigEditBox testPlanCEBox = new ConfigEditBox();

    OddsSimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config, String testSession) {
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

    public void displayInPanel() {
        tbl.clear();
        int row = 0;
        final HorizontalPanel oddsRepositorySitePanel = new HorizontalPanel();
        final HTML lblSiteBoxes = HtmlMarkup.html(SimulatorProperties.oddsRepositorySite);

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator Type"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getActorTypeFullName()));

        row++;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator ID"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getId().toString()));

        row++;

        for (final SimulatorConfigElement ele : config.getElements()) {

            // String
            if (ele.isString()) {
                if (ele.isEditable()) {

                     if (SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT.equals(ele.name)) {
                         testPlanCEBox.configure(ele, tbl, row);
                         testPlanCEBox.setVisible(persistenceCb.getValue());
                     } else if (SimulatorProperties.oddePatientId.equals(ele.name)) {
                         oddePatientIdCEBox.configure(ele,tbl,row);
                         oddePatientIdCEBox.setVisible(persistenceCb.getValue());
                     } else {
                         new ConfigEditBox(ele, tbl, row);
                     }
                } else {
                    new ConfigTextDisplayBox(ele, tbl, row);
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
                                    lblSiteBoxes.setVisible(persistenceOpt);
                                    siteBoxes.setVisible(persistenceOpt);
                                    oddePatientIdCEBox.setVisible(persistenceOpt);
                                    testPlanCEBox.setVisible(persistenceOpt);

                                }
                        }
                    );
                } else {
                    new ConfigBooleanBox(ele, tbl, row);
                }

                row++;
            }

            // Selecting a Repository for the ODDS
            else if (SimulatorProperties.oddsRepositorySite.equals(ele.name)) {
                siteSelectionPresenter = new SiteSelectionPresenter(simulatorControlTab.toolkitService, TransactionType.PROVIDE_AND_REGISTER.getName(), ele.asList(), siteBoxes);

                tbl.setWidget(row, 0, lblSiteBoxes);
                tbl.setWidget(row, 1, siteBoxes);

                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                ele.setValue(siteSelectionPresenter.getSelected());
                            }
                        }
                );
                row++;

                boolean persistenceOpt = persistenceCb.getValue();
                lblSiteBoxes.setVisible(persistenceOpt);
                siteBoxes.setVisible(persistenceOpt);
            }

        }

        hpanel = new HorizontalPanel();

        panel.add(hpanel);
        hpanel.add(tbl);

        hpanel.add(saveButton);
        hpanel.add(HtmlMarkup.html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        panel.add(HtmlMarkup.html("<br />"));

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

    /**
     *
     */
   public void saveSimConfig() {

       // If the persistence option is ON, then saving should activate the register OD transaction.
       if (persistenceCb.getValue()) {
           String errMsg = "";
           if (siteSelectionPresenter!=null) {
               List<String> selectedRepos = siteSelectionPresenter.getSelected();
               List<String> siteNames = siteSelectionPresenter.siteNames;
               if (selectedRepos==null || (selectedRepos!=null && selectedRepos.size()==0)) {
                   errMsg += "The persistence option requires a repository but none are selected. Please select a repository. ";
               } else if (siteNames==null || (siteNames!=null && siteNames.size()==0)) {
                   errMsg += "Persistence option requires a repository but none are found/configured. Please add a Repository using the Simulator Manager or configure a Site that supports a PnR transaction. ";
               }
               if ("".equals(oddePatientIdCEBox.tb.getValue())) {
                   errMsg += "An On-Demand Document Entry Patient ID is required. ";
               }
               if ("".equals(testPlanCEBox.tb.getValue())) {
                   errMsg += "A testplan number to Register an On-Demand Document Entry and to Supply Content is required. ";
               }
           } else {
               errMsg += "siteSelectionPresenter is null! ";
           }

           if (!"".equals(errMsg)) {
               new PopupMessage("Error(s): " + errMsg);
               return;
           }
       }

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

}