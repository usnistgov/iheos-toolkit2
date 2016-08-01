package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od;

import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.SimulatorControlTab;

public class OddsEditTab extends GenericQueryTab {
    /**
     * NOTE: The OddsEditTab cannot extend from query GenericQueryTab because the patientId textbox is synced across all tabs. If the patient Id changes in another tab, it would change the simulator patient id.
     */
    SimulatorConfig config = null;
    SimulatorControlTab simulatorControlTab = null;

    public OddsEditTab(BaseSiteActorManager siteActorManager) {
        super(siteActorManager);
    }

    public OddsEditTab(SimulatorControlTab simulatorControlTab, SimulatorConfig config) {
        super(new FindDocumentsSiteActorManager());
        this.config = config;
        this.simulatorControlTab = simulatorControlTab;
    }

    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        topPanel = new VerticalPanel();


        container.addTab(topPanel, "Odds Sim Edit", select);

        OddsSimConfigMgr simConfigMgr = new OddsSimConfigMgr(simulatorControlTab, topPanel, config, getCurrentTestSession());
        simConfigMgr.displayHeader();
        simConfigMgr.displayInPanel();
    }

    public String getWindowShortName() {
        return "oddssimedit";
    }

}
