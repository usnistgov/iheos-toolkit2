package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.user.client.ui.FlexTable;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;

/**
 *
 */
public class SiteTransactionTable extends FlexTable {
    private int row = 0;

    public SiteTransactionTable(Site site) {
        build(site);
    }

    private void build(Site site) {
        setText(row++, 0, "Endpoints");

        setText(row, 0, "homeCommunityId");
        setText(row++, 1, site.getHome());

        for (TransactionBean trans : site.transactions().transactions) {
            setText(row, 0, trans.getName());
            setText(row++, 1, trans.getEndpoint());
        }

        if (site.repositories().transactions.size() > 0) {
            setText(row++, 0, "Repository");
            for (TransactionBean repo : site.repositories().transactions) {
                setText(row, 0, repo.getName());
                setText(row++, 1, repo.getEndpoint());
            }
        }
    }
}
