package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

/**
 * Created by bill on 9/21/15.
 */
public class PidButtonClickHandler implements ClickHandler {
    SimulatorConfig config = null;
    SimulatorControlTab simulatorControlTab = null;

    public void onClick(ClickEvent event) {
        simulatorControlTab.simConfigSuper.delete(config);
        simulatorControlTab.simConfigSuper.refresh();
        simulatorControlTab.toolkitService.deleteConfig(config, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("pidConfig:" + caught.getMessage());
            }

            public void onSuccess(String result) {
                simulatorControlTab.loadSimStatus();
            }

        });
    }

}
