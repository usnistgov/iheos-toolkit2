package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.intf.SimConfigMgrIntf;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 * Manages the content of a single Simulator on the screen
 * @author bill
 *
 */
public abstract class BaseSimConfigMgr implements SimConfigMgrIntf {
    /**
     *
     */
    private SimulatorControlTab simulatorControlTab;
    FlowPanel panel;
    HorizontalPanel hpanel;
    SimulatorConfig config;
    String testSession;
    FlexTable tbl = new FlexTable();
    Button saveButton = new Button("Save");

    int row = 0;

    public BaseSimConfigMgr(SimulatorControlTab simulatorControlTab, FlowPanel panel, SimulatorConfig config, String testSession) {
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

    public void displayHeader() {

    }

    public void displayBasicSimulatorConfig() {
        tbl.clear();

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator Type"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getActorTypeFullName()));

        row++;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator ID"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getId().toString()));

        row++;
    }

    public void displayInPanel() {

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
                final RGSelectionPresenter rgSelectionPresenter = new RGSelectionPresenter(/*simulatorControlTab.toolkitService, */configEle.asList(), rgBoxes);
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
            
            // Selecting RIGs for the IIG
            else if (SimulatorProperties.respondingImagingGateways.equals(ele.name)) {
               final SimulatorConfigElement configEle = ele;
               HorizontalPanel rigBoxes = new HorizontalPanel();
               final RigSelectionPresenter rigSelectionPresenter = new RigSelectionPresenter(configEle.asList(), rigBoxes);
               tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
               tbl.setWidget(row, 1, rigBoxes);
               saveButton.addClickHandler(
                       new ClickHandler() {
                           @Override
                           public void onClick(ClickEvent clickEvent) {
                               configEle.setValue(rigSelectionPresenter.getSelected());
                           }
                       }
               );
               row++;
            }
            
            // Selecting IDS for the RG
            else if (SimulatorProperties.imagingDocumentSources.equals(ele.name)) {
               final SimulatorConfigElement configEle = ele;
               HorizontalPanel idsBoxes = new HorizontalPanel();
               final IDSSelectionPresenter idsSelectionPresenter = new IDSSelectionPresenter(configEle.asList(), idsBoxes);
               tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
               tbl.setWidget(row, 1, idsBoxes);
               saveButton.addClickHandler(
                  new ClickHandler() {
                      @Override
                      public void onClick(ClickEvent clickEvent) {
                          configEle.setValue(idsSelectionPresenter.getSelected());
                      }
                  }
               );
               row++;
           }

            // Should the RG return an error instead of content?
            else if (SimulatorProperties.errors.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel erBoxes = new HorizontalPanel();
                final ErrorSelectionPresenter erSelectionPresenter = new ErrorSelectionPresenter(/*simulatorControlTab.toolkitService,*/ TransactionType.XC_QUERY.getName(), configEle.asList(), erBoxes);
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
                final PatientErrorMapPresenter presenter = new PatientErrorMapPresenter(map, actorType/*, simulatorControlTab.toolkitService*/);
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
            } else {
                // Base class does not recognize this element, addTest a spacer
                row++;
            }

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
        ClientUtils.INSTANCE.getToolkitServices().putSimConfig(config, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("saveSimConfig:" + caught.getMessage());
            }

            public void onSuccess(String result) {
                // reload simulators to getRetrievedDocumentsModel updates
                if (simulatorControlTab != null)
                    new LoadSimulatorsClickHandler(simulatorControlTab, testSession).onClick(null);
            }
        });
    }

    public int getRow() {
        return row;
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

    public FlowPanel getPanel() {
        return panel;
    }

}