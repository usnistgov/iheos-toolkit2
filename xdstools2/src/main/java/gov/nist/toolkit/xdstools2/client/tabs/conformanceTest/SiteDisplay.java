package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.sitemanagementui.client.Site;
import gov.nist.toolkit.xdstools2.client.widgets.SimSystemAnchor;
import gov.nist.toolkit.xdstools2.client.widgets.SiteTransactionTable;

/**
 *
 */
public class SiteDisplay extends FlowPanel {

    public SiteDisplay(String title, Site site) {
        add(new HTML("<h2>" + title + "</h2>"));
        add(new SimSystemAnchor("System: " + site.getName(), site.siteSpec()));
        add(new HTML("<br />"));
        add(new SiteTransactionTable(site));
    }
}
