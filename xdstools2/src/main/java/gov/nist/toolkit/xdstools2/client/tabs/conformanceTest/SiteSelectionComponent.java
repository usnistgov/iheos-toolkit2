package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.sitemanagement.client.StringSort;
import gov.nist.toolkit.xdstools2.client.Xdstools2;

import java.util.*;

/**
 *
 */
public class SiteSelectionComponent extends Composite implements IsWidget, HasValueChangeHandlers<String> {

    private HorizontalFlowPanel panel = new HorizontalFlowPanel();
    private List<RadioButton> buttons = new ArrayList<>();
    private SiteSelectionComponent me;

    public SiteSelectionComponent(ActorType actorType, String testSession) {
        me = this;
        panel.add(new HTML("Site"));
        panel.add(siteGridForActor(actorType, testSession));
    }

    private Grid siteGridForActor(ActorType actorType, String testSession) {

        Set<Site> sites = new HashSet<Site>();

        List<String> siteNames = new ArrayList<String>();
        for (Site site : sites)
            siteNames.add(site.getName());
        siteNames = StringSort.sort(siteNames);

        if (actorType == null) {
            // any actor type
            for (String actorTypeName : ActorType.getActorNames()) {
                ActorType at = ActorType.findActor(actorTypeName);
                for (TransactionType tt : at.getTransactions()) {
                    sites.addAll(findSites(tt, true  /* tls */, testSession));
                    sites.addAll(findSites(tt, false /* tls */, testSession));
                }
            }
        } else {
            for (TransactionType tt : actorType.getTransactions()) {
                sites.addAll(findSites(tt, true  /* tls */, testSession));
                sites.addAll(findSites(tt, false /* tls */, testSession));
            }
        }

        int cols = 5;
        int row=0;
        int col=0;
        Grid grid = new Grid( sites.size()/cols + 1 , cols);

        for (Site site : sites) {
            final String siteName = site.getName();
            RadioButton rb = new RadioButton("SiteSel", siteName);
            rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                    ValueChangeEvent.fire(me, siteName);
                }
            });

            buttons.add(rb);
            grid.setWidget(row, col, rb);
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }

        }
        return grid;
    }


    // since to has come over from server and tt was generated here, they
    // don't align hashvalues.  Search must be done the old fashion way
    private List<Site> findSites(TransactionType tt, boolean tls, String testSession) {
        Map<TransactionType, List<Site>> map;

        // aka testSession
        String user = testSession;

        if (tls)
            map = Xdstools2.transactionOfferings.tmap;
        else
            map = Xdstools2.transactionOfferings.map;

        for (TransactionType t : map.keySet()) {
            if (t.getName().equals(tt.getName())) {
                List<Site> sitesForTransaction = map.get(t);

                // filter out sites that represent sims and do not match user
                List<Site> sitesForUser = new ArrayList<Site>();
                for (Site s : sitesForTransaction) {
                    if (user.equals(s.user))
                        sitesForUser.add(s);
                }

                return sitesForUser;
            }
        }
        return new ArrayList<>();
    }


    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

}
