package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.command.command.PutSimConfigCommand;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.intf.SimConfigMgrIntf;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.shared.command.request.SimConfigRequest;

import java.util.List;

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
    boolean wasLocked = false;
    String testSession;
    FlexTable tbl = new FlexTable();
    Button saveButton = new Button("Save");

    int row = 0;

    public BaseSimConfigMgr(SimulatorControlTab simulatorControlTab, FlowPanel panel, SimulatorConfig config, String testSession) {
        this.simulatorControlTab = simulatorControlTab;
        this.panel = panel;
        this.config = config;
        this.testSession = testSession;
        if (config.get(SimulatorProperties.locked).asBoolean())
            wasLocked = true;
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
        tbl.setWidget(row, 1, HtmlMarkup.html(config.actorTypeFullName()));

        row++;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator ID"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getId().toString()));

        row++;
    }

    public void displayInPanel() {

        for (SimulatorConfigElement ele : config.getElements()) {

            // String
            if (ele.hasString()) {
                if (ele.isEditable()) {
                    new ConfigEditBox(ele, tbl, row);
                } else {
                    new ConfigTextDisplayBox(ele, tbl, row);
                }
                row++;
            }

            // Boolean
            else if (ele.hasBoolean()) {
                new ConfigBooleanBox(ele, tbl, row);
                row++;
            }

            // List
            else if (ele.hasList()) {
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                FlowPanel values = new FlowPanel();
                List<String> vals = ele.asList();
                for (String val : vals) values.add(new Label(val));
                panel.add(values);
                tbl.setWidget(row, 1, values);
                row++;
            }

            // Selecting RGs for the IG
            else if (SimulatorProperties.respondingGateways.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel rgBoxes = new HorizontalPanel();
                final RGSelectionPresenter rgSelectionPresenter = new RGSelectionPresenter(configEle.asList(), rgBoxes);
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                tbl.setWidget(row, 1, rgBoxes);
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setStringListValue(rgSelectionPresenter.getSelected());
                            }
                        }
                );
                row++;
            }

            // Selecting Repositories behind the RG
            else if (SimulatorProperties.repositories.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel rgBoxes = new HorizontalPanel();
                // more general than the name suggests
                final RepositorySelectionPresenter rgSelectionPresenter = new RepositorySelectionPresenter(configEle.asList(), rgBoxes);
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                tbl.setWidget(row, 1, rgBoxes);
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setStringListValue(rgSelectionPresenter.getSelected());
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
                               configEle.setStringListValue(rigSelectionPresenter.getSelected());
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
                          configEle.setStringListValue(idsSelectionPresenter.getSelected());
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
                                configEle.setStringListValue(erSelectionPresenter.getSelected());
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
                                configEle.setPatientErrorMapValue(map);
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
        boolean locked = wasLocked || config.getConfigEle(SimulatorProperties.locked).asBoolean();
        if (locked) {
            if (PasswordManagement.isSignedIn) {
            }
            else {
                PasswordManagement.addSignInCallback(signedInCallback);

                new AdminPasswordDialogBox(panel);

                return;
            }
        }
        saveSimConfigPreAuthorized();
    }

    public void saveSimConfigPreAuthorized() {
        new PutSimConfigCommand(){
            @Override
            public void onComplete(String result) {
                // reload simulators to getRetrievedDocumentsModel updates
                if (simulatorControlTab != null)
                    new LoadSimulatorsClickHandler(simulatorControlTab, testSession).onClick(null);
            }
        }.run(new SimConfigRequest(ClientUtils.INSTANCE.getCommandContext(),config));
    }

    // Boolean data type ignored
    AsyncCallback<Boolean> signedInCallback = new AsyncCallback<Boolean> () {

        public void onFailure(Throwable ignored) {
        }

        public void onSuccess(Boolean ignored) {
            saveSimConfigPreAuthorized();
        }

    };


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