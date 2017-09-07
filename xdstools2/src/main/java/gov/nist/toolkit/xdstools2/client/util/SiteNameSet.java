package gov.nist.toolkit.xdstools2.client.util;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.StringSort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class SiteNameSet {
    private Set<String> names = new HashSet<>();

    public void add(Site site) {
        names.add(site.getName());
    }

    public List<String> sortedNames() { return StringSort.sort(names);}
}
