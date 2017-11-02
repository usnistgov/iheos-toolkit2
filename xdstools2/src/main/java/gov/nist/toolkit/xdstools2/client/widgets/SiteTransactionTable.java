package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.xdstools2.client.widgets.SiteSelectionWidget_old.SiteSorter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SiteTransactionTable extends FlexTable {
    private int row = 0;

    public SiteTransactionTable(Site site, String title) {
        if (title!=null && !"".equals(title))
            addStyleName(title.replace(" ",""));
        addStyleName("with-border");
        build(site);
    }

    private void build(Site site) {
        SiteSorter sorter = new SiteSorter();
        HTML ei = new HTML("Endpoints and IDs");
        ei.addStyleName("detail-table-header");
        setWidget(row, 0, ei);
        ei = new HTML("Secure");
        ei.addStyleName("detail-table-header");
        setWidget(row, 1, ei);
        ei = new HTML("Non-secure");
        ei.addStyleName("detail-table-header");
        setWidget(row, 2, ei);
        row++;

        boolean hasContent = false;

        for (TransactionBean trans : site.transactions().transactions) {
            sorter.add(trans.getName(), trans.getEndpoint());
        }

        List<String> alreadyDisplayed = new ArrayList<>();
        for (TransactionBean trans : site.transactions().transactions) {
            if (alreadyDisplayed.contains(trans.getName()))
                continue;
            hasContent = true;
            setText(row, 0, trans.getName());
            setText(row, 1, sorter.getTls(trans.getName()));
            setText(row++, 2, sorter.getNoTls(trans.getName()));
            alreadyDisplayed.add(trans.getName());
        }

        sorter = new SiteSorter();
        for (TransactionBean repo : site.repositories().transactions) {
            String label = "Repository (" + repo.getName() + ")";
            sorter.add(label, repo.getEndpoint());
        }

        alreadyDisplayed = new ArrayList<>();
        for (TransactionBean repo : site.repositories().transactions) {
            String label = "Repository (" + repo.getName() + ")";
            if (alreadyDisplayed.contains(label))
                continue;
            hasContent = true;
            setText(row, 0, label);
            setText(row, 1, sorter.getTls(label));
            setText(row++, 2, sorter.getNoTls(label));
            alreadyDisplayed.add(label);
        }

        String home = site.getHome();
        if (home != null && !home.equals("")) {
            hasContent = true;
            setText(row, 0, "homeCommunityId");
            setText(row++, 1, site.getHome());
        }

        if (!hasContent) {
            clear();
            setWidget(0,0, new HTML("No Configuration"));
        }
    }
}
