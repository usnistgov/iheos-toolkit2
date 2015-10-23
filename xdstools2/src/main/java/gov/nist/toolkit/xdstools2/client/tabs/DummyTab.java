package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

/**
 * Created by bill on 9/16/15.
 */
public class DummyTab extends GenericQueryTab {
    public DummyTab() {
        super(new FindDocumentsSiteActorManager());	}

    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
    }

    @Override
    public String getWindowShortName() {
        return "dummy";
    }
}
