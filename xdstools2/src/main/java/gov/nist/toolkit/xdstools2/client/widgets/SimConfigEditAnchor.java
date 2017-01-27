package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.EditTab;

/**
 *
 */
public class SimConfigEditAnchor extends Anchor {

    public SimConfigEditAnchor(final String title, final SimulatorConfig simConfig) {
        super(title);

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                EditTab editTab = new EditTab(null, simConfig);
                editTab.onTabLoad(true, simConfig.getDefaultName());
            }
        });

    }

}
