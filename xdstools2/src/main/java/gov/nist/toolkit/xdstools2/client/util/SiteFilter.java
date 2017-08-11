package gov.nist.toolkit.xdstools2.client.util;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.StringSort;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.ASite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Filter sites
 */
public class SiteFilter {
    // ASite is the site name and enabled/disabled flag
    private List<Site> sites;
    private List<Site> selected = new ArrayList<>();
    private TransactionOfferings transactionOfferings;
    private static List<String> excludedSites = new ArrayList<>();

    static {
        excludedSites.add("GazelleSts");
    }

    public SiteFilter(TransactionOfferings transactionOfferings) {
        this.transactionOfferings = transactionOfferings;
        this.sites = transactionOfferings.getAllSites();
        this.selected.addAll(sites);
    }

    public SiteFilter transactionTypesOnly(List<TransactionType> transactionTypes, boolean includeTls, boolean includeNonTls) {
        Set<Site> keeps = new HashSet<>();
        for (TransactionType transactionType : transactionTypes) {
            if (includeNonTls)
                keeps.addAll(transactionOfferings.map.get(transactionType));
            if (includeTls)
                keeps.addAll(transactionOfferings.tmap.get(transactionType));
        }

        filter(keeps);
        return this;
    }

    public SiteFilter simulatorsOnly() {
        Set<Site> keeps = new HashSet<>();
        for (Site s : selected)
            if (s.isSimulator())
                keeps.add(s);
        filter(keeps);
        return this;
    }

    public SiteFilter staticSitesOnly() {
        Set<Site> keeps = new HashSet<>();
        for (Site s : selected)
            if (!s.isSimulator())
                keeps.add(s);
        filter(keeps);
        return this;
    }

    public List<ASite> sorted() {
        List<ASite> out = new ArrayList<>();

        List<String> names = new ArrayList<>();
        for (Site s : sites) {
            if (excludedSites.contains(s.getName())) continue;
            names.add(s.getName());
        }

        names = StringSort.sort(names);

        for (String name : names) {
            Site s = getSite(name);
            if (s != null) {
                ASite aSite = new ASite(selected.contains(s), s.getName());
                out.add(aSite);
            }
        }

        return out;
    }

    private void filter(Set<Site> keeps) {
        List<Site> newSelected = new ArrayList<>();
        newSelected.addAll(selected);

        for (Site s : selected)
            if (!keeps.contains(s))
                newSelected.remove(s);

        selected = newSelected;
    }

    private Site getSite(String name) {
        for (Site s : sites)
            if (s.getName().equals(name)) return s;
        return null;
    }
}
