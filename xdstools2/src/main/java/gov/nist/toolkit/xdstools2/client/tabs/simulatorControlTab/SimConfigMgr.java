package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actorfactory.SimulatorProperties;
import gov.nist.toolkit.actorfactory.client.PatientError;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import java.util.List;

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
    VerticalPanel panel;
    HorizontalPanel hpanel;
    SimulatorConfig config;
    String testSession;
    FlexTable tbl = new FlexTable();
    Button saveButton = new Button("Save");

    SimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config, String testSession) {
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
                                config.updateDocTypeSelection();
                                saveSimConfig();
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
                                config.updateDocTypeSelection();
                                saveSimConfig();
                            }
                        }
                );
                row++;
            }

            else if (SimulatorProperties.errorForPatient.equals(ele.name)) {
                List<TransactionType> transactionTypes = ActorType.findActor(config.getActorType()).getTransactions();
                PatientErrorSelectionPresenter presenter = new PatientErrorSelectionPresenter(patientErrorList, simulatorControlTab.toolkitService, transactionType, new SaveHandler<List<PatientError>>() {
                    @Override
                    public void onSave(List<PatientError> var) {

                    }
                });
            }

//            Image img = new Image("icons/add-button.png");
//            Anchor b = new Anchor();
//            b.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent clickEvent) {
//                    new PopupMessage("Click");
//                }
//            });
//            b.getElement().appendChild(img.getElement());
//            tbl.setWidget(row, 0, new HTML("Button"));
//            tbl.setWidget(row++, 1, b);
        }

        hpanel = new HorizontalPanel();

        panel.add(hpanel);
        hpanel.add(tbl);

        hpanel.add(saveButton);
        hpanel.add(HtmlMarkup.html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        panel.add(HtmlMarkup.html("<br />"));

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