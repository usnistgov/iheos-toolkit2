package gov.nist.toolkit.results.shared;

import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actortransaction.shared.SimId;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.actortransaction.shared.SiteSpec;

/**
 *
 */

public class SiteBuilder {

    static public Site siteFromSiteSpec(SiteSpec siteSpec, String sessionId) throws Exception {
        Site site = SiteServiceManager.getSiteServiceManager().getSite(sessionId, siteSpec.name);
        return site;
    }

    static public SiteSpec siteSpecFromSimId(SimId simId) {
        return new SiteSpec(simId.toString(), ActorType.findActor(simId.getActorType()), null);
    }

    static public SiteSpec siteSpecFromSite(Site site) {
        SiteSpec siteSpec = new SiteSpec();
        siteSpec.name = site.getName();
        siteSpec.homeId = site.getHome();

        return siteSpec;
    }
}
