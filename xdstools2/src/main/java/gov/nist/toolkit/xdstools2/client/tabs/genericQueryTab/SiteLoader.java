package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.StringSort;

import java.util.*;

public class SiteLoader {
    private final GenericQueryTab genericQueryTab;

    public SiteLoader(GenericQueryTab genericQueryTab) {
        this.genericQueryTab = genericQueryTab;
    }

    // since to has come over from server and tt was generated here, they
    // don't align hashvalues.  Search must be done the old fashion way
    List<Site> findSites(TransactionType tt, boolean tls) {
        Map<TransactionType, List<Site>> map;

        // aka testSession
        String user = genericQueryTab.getTestSessionManager().getCurrentTestSession();

        if (tls) {
            map = GenericQueryTab.transactionOfferings.tmap;
        } else {
            map = GenericQueryTab.transactionOfferings.map;
        }

//        Xdstools2.DEBUG("findSites for " + user);
        for (TransactionType t : map.keySet()) {
            if (t == null) continue; //Window.alert("TransactionType keys " + map.keySet().size());
            if (t.getName().equals(tt.getName())) {
                List<Site> sitesForTransaction = map.get(t);
//                Xdstools2.DEBUG("tmap(" + t  + ") is " + sitesForTransaction);
                if (user == null) return sitesForTransaction;

                // validate out sites that represent sims and do not match user
                List<Site> sitesForUser = new ArrayList<Site>();
                for (Site s : sitesForTransaction) {
//                    Xdstools2.DEBUG("site " + s.getName() + " has user " + s.user);
                    if (s.getTestSession() == null)
                        sitesForUser.add(s);
                    else if (user.equals(s.getTestSession())) {
                        sitesForUser.add(s);
                    }
                }

                return sitesForUser;
            }
        }
        return new ArrayList<>();
    }

    public List<RadioButton> addSitesForActor(ActorType actorType, int majorRow) {

        Set<Site> sites = new HashSet<Site>();

        List<String> siteNames = new ArrayList<String>();
        for (Site site : sites)
            siteNames.add(site.getName());
        siteNames = StringSort.sort(siteNames);

        for (TransactionType tt : actorType.getTransactions()) {
            sites.addAll(findSites(tt, true  /* tls */));
            sites.addAll(findSites(tt, false /* tls */));
        }

        int cols = 5;
        int row=0;
        int col=0;
        Grid grid = new Grid( sites.size()/cols + 1 , cols);
        List<RadioButton> buttons = new ArrayList<RadioButton>();

        SiteSpec commonSiteSpec = genericQueryTab.getCommonSiteSpec();

        for (Site site : sites) {
            String siteName = site.getName();
            RadioButton rb = new RadioButton(actorType.getName(), siteName);

            if (
                    commonSiteSpec.getName().equals(actorType.getName())
                //	&& commonSiteSpec.getActorType() == actorType
                    )
                rb.setValue(true);
            if (
                    commonSiteSpec.getName().equals(siteName)
                //	&& commonSiteSpec.getActorType() == actorType
                    )
                rb.setValue(true);

            buttons.add(rb);
            grid.setWidget(row, col, rb);
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }

        }
        genericQueryTab.mainGrid.setWidget(majorRow, 1, grid);

        return buttons;
    }

}