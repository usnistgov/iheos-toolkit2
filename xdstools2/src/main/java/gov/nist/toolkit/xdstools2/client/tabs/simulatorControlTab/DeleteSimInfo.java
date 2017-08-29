package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.ClickHandlerData;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.List;

public class DeleteSimInfo {
FlowPanel containerPanel;
SimulatorControlTab hostTab;
List<SimInfo> simInfoList;

    public DeleteSimInfo(FlowPanel containerPanel, SimulatorControlTab hostTab) {
        this.containerPanel = containerPanel;
        this.hostTab = hostTab;
    }

    public void setSimInfoList(List<SimInfo> simInfoList) {
        this.simInfoList = simInfoList;
    }

    private boolean hasLockedSimulators() {
        boolean locked = false;
        for (SimInfo simInfo : simInfoList) {
            SimulatorConfigElement ele =
            simInfo.getSimulatorConfig().getConfigEle(SimulatorProperties.locked);
            locked = (ele == null) ? false : ele.asBoolean();
            if (locked) {
                break;
            }
        }

        return locked;

    }


    public void delete() {

        if (hasLockedSimulators()) {
            if (PasswordManagement.isSignedIn) {
                doDelete();
            }
            else {
                PasswordManagement.addSignInCallback(
                        new AsyncCallback<Boolean>() {

                            public void onFailure(Throwable ignored) {
                            }

                            public void onSuccess(Boolean ignored) {
                                doDelete();
                            }

                        }
                );

                new AdminPasswordDialogBox(containerPanel);

                return;
            }
        } else {
            doDelete();
        }
    }
    private void doDelete() {
        String simIds = "";

        if (simInfoList==null)
            return;

        if (simInfoList!=null) {
           for (SimInfo simInfo : simInfoList) {
               simIds +=  simInfo.getSimulatorConfig().getId().toString() + "<br/>";
           }
        }

        VerticalPanel body = new VerticalPanel();
        body.add(new HTML("<p>Delete the following simulator(s)?<br/>" + simIds + "</p>"));


        Button actionButton = new Button("Yes");
        actionButton.addClickHandler(
                new ClickHandlerData<List<SimInfo>>(simInfoList) {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        int cx = 1;
                        for (SimInfo simInfo : simInfoList) {
                            try {
                                DeleteButtonClickHandler handler = new DeleteButtonClickHandler(hostTab, simInfo.getSimulatorConfig());
                                handler.delete(cx==simInfoList.size());

                            } catch (Exception ex) {
                                Window.alert("Delete failed simId: " + simInfo.getSimulatorConfig().getId().toString() + ". Exception: " + ex.toString());
                            }
                            cx++;
                        }
                    }
                }
        );
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/garbage.png\" title=\"Delete\" height=\"16\" width=\"16\"/>");
        safeHtmlBuilder.appendHtmlConstant("Confirm Delete Simulator");
        new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionButton);
    }
}
