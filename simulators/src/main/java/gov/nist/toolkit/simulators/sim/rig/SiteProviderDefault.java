package gov.nist.toolkit.simulators.sim.rig;

import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.util.List;

/**
 * Created by davidmaffitt on 3/10/17.
 */
public class SiteProviderDefault implements SiteProvider {

    @Override
    public Sites getSites(List<String> siteNames) throws Exception {
         SimManager simMgr = new SimManager("ignored");
         List<Site> sites = simMgr.getSites(siteNames);
         return new Sites(sites);
    }
}
