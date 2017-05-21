package gov.nist.toolkit.desktop.client.legacy.widgets;

import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.GetSimConfigsCommand;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimConfigsRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Multi-anchor that contains links to both system config and sim config (if it is a sim)
 */
public class SimSystemAnchor extends HorizontalFlowPanel {

    public SimSystemAnchor(String title, final SiteSpec siteSpec) {
        add(new HTML(title));

        List<SimId> simIds = new ArrayList<>();
        simIds.add(new SimId(siteSpec));

        new GetSimConfigsCommand(){
            @Override
            public void onComplete(List<SimulatorConfig> simulatorConfigs) {
                if (simulatorConfigs.size() == 1) {
                    final SimulatorConfig simConfig = simulatorConfigs.get(0);
                    add(new SimConfigEditAnchor("[Simulator Configuration]", simConfig));
                    add(new SimLogViewerAnchor("[Simulator Log]", simConfig.getId()));
                } else {
                    add(new SiteEditAnchor("[System Configuration]", siteSpec));
                }
            }
        }.run(new GetSimConfigsRequest(XdsTools2Presenter.data().getCommandContext(),simIds));
    }
}
