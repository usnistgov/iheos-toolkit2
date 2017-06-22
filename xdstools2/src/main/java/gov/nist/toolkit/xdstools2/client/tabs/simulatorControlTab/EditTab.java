package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

/**
 *
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

    @Override
    protected Widget buildUI() {
        return null;
    }

    @Override
    protected void bindUI() {

    }

    @Override
    protected void configureTabView() {

    }

    @Override
    public void onTabLoad(boolean select, String eventName) {
        registerTab(select, eventName);

        SimConfigMgr simConfigMgr = new SimConfigMgr(simulatorControlTab, tabTopPanel, config, getCurrentTestSession());
        simConfigMgr.displayInPanel();
    }

    public String getWindowShortName() {
        return "simedit";
    }

}
