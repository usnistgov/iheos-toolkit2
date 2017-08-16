package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.ClickHandlerData;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

public class DeleteSimInfo {
FlowPanel containerPanel;
SimulatorControlTab hostTab;

    public DeleteSimInfo(FlowPanel containerPanel, SimulatorControlTab hostTab) {
        this.containerPanel = containerPanel;
        this.hostTab = hostTab;
    }

    public void delete(final SimInfo simInfo) {
        SimulatorConfigElement ele = simInfo.getSimulatorConfig().getConfigEle(SimulatorProperties.locked);
        boolean locked = (ele == null) ? false : ele.asBoolean();
        if (locked) {
            if (PasswordManagement.isSignedIn) {
                doDelete(simInfo.getSimulatorConfig());
            }
            else {
                PasswordManagement.addSignInCallback(
                        new AsyncCallback<Boolean>() {

                            public void onFailure(Throwable ignored) {
                            }

                            public void onSuccess(Boolean ignored) {
                                doDelete(simInfo.getSimulatorConfig());
                            }

                        }
                );

                new AdminPasswordDialogBox(containerPanel);

                return;
            }
        } else {
            doDelete(simInfo.getSimulatorConfig());
        }
    }
    private void doDelete(SimulatorConfig config) {
        VerticalPanel body = new VerticalPanel();
        body.add(new HTML("<p>Delete " + config.getId().toString() + "?</p>"));
        Button actionButton = new Button("Yes");
        actionButton.addClickHandler(
                new ClickHandlerData<SimulatorConfig>(config) {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        SimulatorConfig config = getData();
                        DeleteButtonClickHandler handler = new DeleteButtonClickHandler(hostTab, config);
                        handler.delete();
                    }
                }
        );
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/garbage.png\" title=\"Delete\" height=\"16\" width=\"16\"/>");
        safeHtmlBuilder.appendHtmlConstant("Confirm Delete Simulator");
        new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionButton);
    }
}
