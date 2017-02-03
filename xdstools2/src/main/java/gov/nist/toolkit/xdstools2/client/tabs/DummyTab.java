package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

/**
 *
 */
public class DummyTab extends GenericQueryTab {
    public DummyTab() {
        super(new FindDocumentsSiteActorManager());	}

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

    }

    @Override
    public String getWindowShortName() {
        return "dummy";
    }
}
