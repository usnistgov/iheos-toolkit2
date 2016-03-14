package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SiteLoader {
    TransactionOfferings transactionOfferings;
    String user;

    // if user is not null it is a filter for sites
    public SiteLoader(TransactionOfferings transactionOfferings, String user) {
        this.transactionOfferings = transactionOfferings;
        this.user = user;
    }

    // since to has come over from server and tt was generated here, they
    // don't align hashvalues.  Search must be done the old fashion way
    List<Site> getSitesForTransactionType(TransactionType tt,  TransactionOptions options) {
        Map<TransactionType, List<Site>> map;

        if (options.isTls()) {
            map = transactionOfferings.tmap;
        } else {
            map = transactionOfferings.map;
        }

        TransactionType t = getTransactionTypeByName(map, tt.getName());
        if (t == null)
            return new ArrayList<>();


        if (user == null)
            return map.get(t);

        List<Site> sitesForUser = new ArrayList<Site>();
        for (Site s : map.get(t)) {
            if (user.equals(s.user)) {
                sitesForUser.add(s);
            }
        }

        return sitesForUser;
    }

    TransactionType getTransactionTypeByName(Map<TransactionType, List<Site>> map, String transactionName) {
        for (TransactionType t : map.keySet())
            if (t.getName().equals(transactionName))
                return t;
        return null;
    }

}