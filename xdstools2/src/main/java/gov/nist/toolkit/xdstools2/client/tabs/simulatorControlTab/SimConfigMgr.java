package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
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

//			tbl.setCellPadding(2);
//			tbl.setCellSpacing(2);
//			tbl.setBorderWidth(1);
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
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getActorType()));

        row++;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator ID"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getId().toString()));

        row++;

        for (SimulatorConfigElement ele : config.getElements()) {
            if (ele.isString()) {
                if (ele.isEditable()) {
                    new ConfigEditBox(ele, tbl, row);
                } else {
                    new ConfigTextDisplayBox(ele, tbl, row);
                }
                row++;
            } else if (ele.isBoolean()) {
                new ConfigBooleanBox(ele, tbl, row);
                row++;
            } else if (ele.isMultiList()) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel rgBoxes = new HorizontalPanel();
                final RGSelectionPresenter rgSelectionPresenter = new RGSelectionPresenter(simulatorControlTab.toolkitService, configEle.asList(), rgBoxes);
//                final RGSelectionPresenter rgSelectionPresenter = getRGSelectionPresenter(rgBoxes);
                tbl.setWidget(row, 0, HtmlMarkup.html("Responding Gateways"));
                tbl.setWidget(row, 1, rgBoxes);
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setValue(rgSelectionPresenter.getSelected());
                                config.updateDocTypeSelection();
                                simulatorControlTab.toolkitService.putSimConfig(config, new AsyncCallback<String>() {

                                    public void onFailure(Throwable caught) {
                                        new PopupMessage("saveSimConfig:" + caught.getMessage());
                                    }

                                    public void onSuccess(String result) {
                                        // reload simulators to get updates
                                        new LoadSimulatorsClickHandler(simulatorControlTab, testSession).onClick(null);

                                    }
                                });
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
    }

//    RGSelectionPresenter getRGSelectionPresenter(Panel panel) {
//        if (config.areRGSitesNecessary()) return new RGSelectionPresenter(simulatorControlTab.toolkitService, config.rgSiteNames, panel);
//        return null;
//    }




}