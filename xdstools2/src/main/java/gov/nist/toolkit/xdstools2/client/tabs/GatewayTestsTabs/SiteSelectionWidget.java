package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.sitemanagement.client.StringSort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class SiteSelectionWidget {
    private final SiteLoader siteLoader;
    CoupledTransactions couplings;
    // configured sites organized by transactions offered
    TransactionOfferings transactionOfferings = null;
    // transaction types that must be configured for the site to be listed
    List<TransactionType> transactionTypes;
    public TransactionSelectionManager transactionSelectionManager = null;
    TransactionOptions transactionOptions = new TransactionOptions();

    Panel panel = new VerticalPanel();
    HorizontalPanel optionsPanel = new HorizontalPanel();
    HorizontalPanel sitePanel = new HorizontalPanel();
    FlexTable siteGrid = new FlexTable();

    CheckBox tlsCheckBox = new CheckBox("TLS");

    public SiteSelectionWidget(TransactionOfferings transactionOfferings, List<TransactionType> transactionTypes, CoupledTransactions couplings, String user) {
        this.transactionOfferings = transactionOfferings;
        this.transactionTypes = transactionTypes;
        this.couplings = couplings;

        siteLoader = new SiteLoader(transactionOfferings, user);
    }

    public Panel build(SiteSpec selectedSite, String displayLabel) {
        optionsPanel.add(tlsCheckBox);
        panel.add(optionsPanel);

        sitePanel.add(new HTML((displayLabel == null) ? "Send to" : displayLabel));
        sitePanel.add(siteGrid);
        panel.add(sitePanel);

        if (selectedSite != null)
            tlsCheckBox.setValue(selectedSite.isTls());

        int siteGridRow = 0;
        Set<String> actorTypeNamesAlreadyDisplayed = new HashSet<>();
        for (TransactionType tt : transactionTypes) {
            Set<ActorType> ats = ActorType.getActorTypes(tt);
            for (ActorType at : ats) {
                String actorTypeName = at.getName();
                if (!actorTypeNamesAlreadyDisplayed.contains(actorTypeName) && at.showInConfig()) {
                    actorTypeNamesAlreadyDisplayed.add(actorTypeName);

                    siteGrid.setWidget(siteGridRow, 0, new HTML(at.getName()));
                    siteGrid.setWidget(siteGridRow++, 1, getSiteTableWidgetforTransactions(tt));
                }
            }
        }

        return panel;
    }

    Widget getSiteTableWidgetforTransactions(TransactionType tt) {
        updateTransactionOptions();
        if (transactionSelectionManager == null)
            transactionSelectionManager = new TransactionSelectionManager(couplings, transactionOptions);
        List<Site> sites = getSiteList(tt);
        transactionSelectionManager.addTransactionType(tt, sites);

        int cols = 5;
        int row=0;
        int col=0;
        Grid grid = new Grid( sites.size()/cols + 1 , cols);
        for (RadioButton rb : transactionSelectionManager.getRadioButtons(tt)) {
            grid.setWidget(row, col, rb);
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }
        return grid;
    }

    List<Site> getSiteList(TransactionType tt) {
        updateTransactionOptions();
        List<Site> sites = siteLoader.getSitesForTransactionType(tt, transactionOptions);

//        sites = sort(sites);

        return sites;
    }

    List<Site> sort(List<Site> sites) {
        List<String> siteNames = new ArrayList<String>();
        for (Site site : sites)
            siteNames.add(site.getName());
        siteNames = StringSort.sort(siteNames);

        List<Site> orderedSites = new ArrayList<Site>();
        for (String siteName : siteNames) {
            for (Site site : sites) {
                if (siteName.equals(site.getName())) {
                    orderedSites.add(site);
                    break;
                }
            }
        }
        return orderedSites;
    }

    void updateTransactionOptions() {
        transactionOptions.setTls(isTLS());
    }

    boolean isTLS() {
        return tlsCheckBox.getValue();
    }

}
