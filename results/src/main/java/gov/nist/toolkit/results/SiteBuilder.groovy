package gov.nist.toolkit.results

import gov.nist.toolkit.actorfactory.SiteServiceManager
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.sitemanagement.client.Site
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class SiteBuilder {

    static public Site siteFromSiteSpec(SiteSpec siteSpec, String sessionId) {
        Site site = SiteServiceManager.siteServiceManager.getSite(sessionId, siteSpec.name)
        return site
    }

    static public SiteSpec siteSpecFromSimId(SimId simId) {
        return new SiteSpec(simId.toString(), ActorType.findActor(simId.actorType), null)
    }

    static public SiteSpec siteSpecFromSite(Site site) {
        SiteSpec siteSpec = new SiteSpec();
        siteSpec.name = site.name
        siteSpec.homeId = site.getHome()

        return siteSpec
    }
}
