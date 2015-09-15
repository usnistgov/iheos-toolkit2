package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

/**
 * Created by bill on 9/15/15.
 */
public class EditTab extends GenericQueryTab {
    SimulatorConfig config = null;
    SimulatorControlTab simulatorControlTab = null;

    public EditTab(BaseSiteActorManager siteActorManager) {
        super(siteActorManager);
    }

    public EditTab(SimulatorControlTab simulatorControlTab, SimulatorConfig config) {
        super(new FindDocumentsSiteActorManager());
        this.config = config;
        this.simulatorControlTab = simulatorControlTab;
    }

    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        topPanel = new VerticalPanel();


        container.addTab(topPanel, "Sim Edit", select);
        addCloseButton(container, topPanel, null);

        SimConfigMgr simConfigMgr = new SimConfigMgr(simulatorControlTab, topPanel, config, myContainer.getTestSessionState());
        simConfigMgr.displayInPanel();
    }

    public String getWindowShortName() {
        return "simedit";
    }

}
