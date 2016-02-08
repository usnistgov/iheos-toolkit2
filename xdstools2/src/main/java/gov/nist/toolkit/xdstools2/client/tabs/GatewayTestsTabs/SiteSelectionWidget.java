package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;

import java.util.List;

/**
 *
 */
public class SiteSelectionWidget {
    CoupledTransactions couplings;
    // configured sites organized by transactions offered
    TransactionOfferings transactionOfferings = null;
    // transaction types that must be configured for the site to be listed
    List<TransactionType> transactionTypes;

    // enable/disable use of these options
    boolean tlsVisible = true;
    boolean tlsEnabled = false;
    //    boolean samlEnabled = false;
    boolean asyncEnabled = false;

    public boolean doASYNC = false;

    Panel panel = new VerticalPanel();
    HorizontalPanel optionsPanel = new HorizontalPanel();
    HorizontalPanel sitePanel = new HorizontalPanel();
    FlexTable siteGrid = new FlexTable();

    CheckBox tlsCheckBox = new CheckBox("TLS");

    public SiteSelectionWidget(TransactionOfferings transactionOfferings, List<TransactionType> transactionTypes) {
        this.transactionOfferings = transactionOfferings;
        this.transactionTypes = transactionTypes;
        panel.add(optionsPanel);
        optionsPanel.add(tlsCheckBox);
        panel.add(sitePanel);
        sitePanel.add(new HTML("Send to"));
        sitePanel.add(siteGrid);
    }

    /*
    public Panel build(SiteSpec selectedSite) {
        // two columns - title and contents
        final int titleColumn = 0;
        final int contentsColumn = 1;
        int commonGridRow = 0;

        tlsCheckBox.setVisible(tlsVisible);
        tlsCheckBox.setEnabled(tlsEnabled);

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
    */
}
