package gov.nist.toolkit.simulators.sim.rig;

import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidmaffitt on 3/10/17.
 */
public class SiteProviderTest implements SiteProvider {
    @Override
    public Sites getSites(List<String> siteNames) throws Exception {
        List<Site> sites = new ArrayList<>();
        for( String siteName: siteNames) {
            Site site = new Site();
        }
        return new Sites(sites);
    }
}
