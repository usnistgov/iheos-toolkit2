package gov.nist.toolkit.results.client;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.simcommon.client.*;
import gov.nist.toolkit.sitemanagement.client.*;

/**
 *
 */

public class SiteBuilder {


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
