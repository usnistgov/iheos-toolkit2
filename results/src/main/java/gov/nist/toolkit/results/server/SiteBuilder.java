package gov.nist.toolkit.results.server;

import gov.nist.toolkit.simcommon.server.*;
import gov.nist.toolkit.sitemanagement.client.*;

/**
 *
 */

public class SiteBuilder {

    static public Site siteFromSiteSpec(SiteSpec siteSpec, String sessionId) throws Exception {
        Site site = SiteServiceManager.getSiteServiceManager().getSite(sessionId, siteSpec.name, siteSpec.testSession);
        return site;
    }
}
