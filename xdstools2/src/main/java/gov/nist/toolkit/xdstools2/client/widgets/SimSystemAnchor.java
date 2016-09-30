package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Multi-anchor that contains links to both system config and sim config (if it is a sim)
 */
public class SimSystemAnchor extends HorizontalFlowPanel {

    public SimSystemAnchor(String title, final SiteSpec siteSpec) {
        add(new HTML(title));
        add(new SiteEditAnchor("[System Configuration]", siteSpec));

        List<SimId> simIds = new ArrayList<>();
        simIds.add(new SimId(siteSpec));

        ClientUtils.INSTANCE.getToolkitServices().getSimConfigs(simIds, new AsyncCallback<List<SimulatorConfig>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Error loading Sim Config for " + siteSpec + "\n" + throwable.getMessage());
            }

            @Override
            public void onSuccess(List<SimulatorConfig> simulatorConfigs) {
                if (simulatorConfigs.size() == 1) {
                    final SimulatorConfig simConfig = simulatorConfigs.get(0);
                    add(new SimConfigEditAnchor("[Simulator Configuration]", simConfig));
                }
            }
        });





    }
}
