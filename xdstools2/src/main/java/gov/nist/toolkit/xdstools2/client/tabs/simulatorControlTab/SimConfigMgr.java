package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

/**
 * Manages the content of a single Simulator on the screen
 * @author bill
 *
 */
class SimConfigMgr {
    /**
     *
     */
    private SimulatorControlTab simulatorControlTab;
    Panel panel;
    HorizontalPanel hpanel;
    SimulatorConfig config;
    String testSession;
    FlexTable tbl = new FlexTable();
    Button saveButton = new Button("Save");

    SimConfigMgr(SimulatorControlTab simulatorControlTab, Panel panel, SimulatorConfig config, String testSession) {
        this.simulatorControlTab = simulatorControlTab;
        this.panel = panel;
        this.config = config;
        this.testSession = testSession;
    }

    void removeFromPanel() {
        if (hpanel != null) {
            panel.remove(hpanel);
            hpanel = null;
        }
    }

    void displayInPanel() {
        tbl.clear();
        int row = 0;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator Type"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getActorTypeFullName()));

        row++;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator ID"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getId().toString()));

        row++;

        for (SimulatorConfigElement ele : config.getElements()) {

            // String
            if (ele.isString()) {
                if (ele.isEditable()) {
                    new ConfigEditBox(ele, tbl, row);
                } else {
                    new ConfigTextDisplayBox(ele, tbl, row);
                }
                row++;
            }

            // Boolean
            else if (ele.isBoolean()) {
                new ConfigBooleanBox(ele, tbl, row);
                row++;
            }

            // Selecting RGs for the IG
            else if (SimulatorProperties.respondingGateways.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel rgBoxes = new HorizontalPanel();
                final RGSelectionPresenter rgSelectionPresenter = new RGSelectionPresenter(simulatorControlTab.toolkitService, configEle.asList(), rgBoxes);
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                tbl.setWidget(row, 1, rgBoxes);
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setValue(rgSelectionPresenter.getSelected());
//                                config.updateDocTypeSelection();
//                                saveSimConfig();
                            }
                        }
                );
                row++;
            }

            // Should the RG return an error instead of content?
            else if (SimulatorProperties.errors.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel erBoxes = new HorizontalPanel();
                final ErrorSelectionPresenter erSelectionPresenter = new ErrorSelectionPresenter(simulatorControlTab.toolkitService, TransactionType.XC_QUERY.getName(), configEle.asList(), erBoxes);
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                tbl.setWidget(row, 1, erBoxes);
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setValue(erSelectionPresenter.getSelected());
  //                              saveSimConfig();
                            }
                        }
                );
                row++;
            }


            else if (SimulatorProperties.errorForPatient.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
//                List<TransactionType> transactionTypes = ActorType.findActor(config.getActorType()).getTransactions();
                ActorType actorType = ActorType.findActor(config.getActorType());
                final PatientErrorMap map = config.getConfigEle(SimulatorProperties.errorForPatient).asPatientErrorMap();
                final PatientErrorMapPresenter presenter = new PatientErrorMapPresenter(map, actorType, simulatorControlTab.toolkitService);
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                tbl.setWidget(row, 1, presenter.asWidget());
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setValue(map);
//                                saveSimConfig();
                            }
                        }
                );
                row++;
            }

            // Selecting a Repository for the ODDS
            else if (SimulatorProperties.oddsRepositorySite.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel siteBoxes = new HorizontalPanel();
                final SiteSelectionPresenter siteSelectionPresenter = new SiteSelectionPresenter(simulatorControlTab.toolkitService, TransactionType.PROVIDE_AND_REGISTER.getName(), configEle.asList(), siteBoxes);
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                tbl.setWidget(row, 1, siteBoxes);
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setValue(siteSelectionPresenter.getSelected());
                                saveSimConfig();
                            }
                        }
                );
                row++;
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

    void saveSimConfig() {
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