package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteConfigCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SimConfigRequest;

/**
 * Created by bill on 9/21/15.
 */
public class PidButtonClickHandler implements ClickHandler {
    SimulatorConfig config = null;
    SimulatorControlTab simulatorControlTab = null;

    public void onClick(ClickEvent event) {
        simulatorControlTab.simConfigSuper.delete(config);
        simulatorControlTab.simConfigSuper.refresh();
        new DeleteConfigCommand(){
            @Override
            public void onComplete(String result) {
                simulatorControlTab.loadSimStatus();
            }
        }.run(new SimConfigRequest(ClientUtils.INSTANCE.getCommandContext(),config));
    }

}
