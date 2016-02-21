package gov.nist.toolkit.results

import gov.nist.toolkit.actorfactory.SiteServiceManager
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class SiteBuilder {

    static public Site siteFromSiteSpec(SiteSpec siteSpec, String sessionId) {
        Site site = SiteServiceManager.siteServiceManager.getSite(sessionId, siteSpec.name)
        if (site == null) return null
        if (siteSpec.actorType) {
            if (site.hasActor(siteSpec.actorType))
                throw new ToolkitRuntimeException('SiteBuilder: site ' + site.name + ' does not contains ActorType ' + siteSpec.actorType)
        }
        return site
    }

    static public SiteSpec siteSpecFromSimId(SimId simId) {
        return new SiteSpec(simId.toString(), ActorType.findActor(simId.actorType), null)
    }
}
