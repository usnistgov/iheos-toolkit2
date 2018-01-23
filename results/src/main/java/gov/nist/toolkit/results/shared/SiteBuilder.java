package gov.nist.toolkit.results.shared;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.server.SiteServiceManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 *
 */

public class SiteBuilder {

    static public Site siteFromSiteSpec(SiteSpec siteSpec, String sessionId) throws Exception {
        Site site = SiteServiceManager.getSiteServiceManager().getSite(sessionId, siteSpec.name, siteSpec.testSession);
        return site;
    }

    static public SiteSpec siteSpecFromSimId(SimId simId) {
        return new SiteSpec(simId.toString(), ActorType.findActor(simId.getActorType()), null, simId.getTestSession());
    }

    static public SiteSpec siteSpecFromSite(Site site) {
        SiteSpec siteSpec = new SiteSpec(site.getTestSession());
        siteSpec.name = site.getName();
        siteSpec.homeId = site.getHome();

        return siteSpec;
    }
}
