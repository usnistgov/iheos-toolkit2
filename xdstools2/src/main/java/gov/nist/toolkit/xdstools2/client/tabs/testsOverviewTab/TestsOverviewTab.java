package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

/**
 * Created by Diane Azais local on 9/23/2015.
 */
public class TestsOverviewTab extends GenericQueryTab {

    GenericQueryTab genericQueryTab;

    // this super is kinda useless now - was a good idea for documentation at one time
    public TestsOverviewTab(){
    super(new FindDocumentsSiteActorManager());
    }

    // Tab initialization
    @Override
    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        // Panel to build inside of
        topPanel = new VerticalPanel();

        genericQueryTab = this;   // share with other methods

        container.addTab(topPanel, "Tests Overview", select);  // link into container/tab management
        addCloseButton(container, topPanel, null);   // add the close button

        HTML title = new HTML();
        title.setHTML("<h2>Tests Overview</h2>");
        topPanel.add(title);

    }

    @Override
    public String getWindowShortName() {
        return "testsoverview";
    }
}
