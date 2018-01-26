package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SiteFactory {
    static public Site getSite(SiteSpec siteSpec) {
        Site site = new Site();
        site.setName(siteSpec.name);
        site.setTestSession(siteSpec.testSession);
        site.validate();
        return site;
    }

    static public List<String> getSiteNames(List<Site> sites) {
        Set<String> names = new HashSet<>();

        for (Site site : sites) {
            names.add(site.getName());
        }
        List<String> returns = new ArrayList<>();
        returns.addAll(names);
        return returns;
    }
}
