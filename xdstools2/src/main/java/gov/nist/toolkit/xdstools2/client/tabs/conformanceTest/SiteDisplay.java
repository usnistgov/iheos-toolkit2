package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.widgets.SimSystemAnchor;
import gov.nist.toolkit.xdstools2.client.widgets.SiteTransactionTable;

/**
 *
 */
public class SiteDisplay extends FlowPanel {

    public SiteDisplay(String title, Site site) {
        this(title, null, site);
    }

    public SiteDisplay(String title, Widget documentation, Site site) {
        add(new HTML("<h2>" + title + "</h2>"));
        if (documentation != null)
            add(documentation);
        add(new HTML("<br />"));
        add(new SimSystemAnchor(
                (site.isSimulator()) ? "Simulator: " + site.getName() : site.getName(),
                site.siteSpec()));
        add(new HTML("<br />"));
        add(new SiteTransactionTable(site,title));
    }
}
