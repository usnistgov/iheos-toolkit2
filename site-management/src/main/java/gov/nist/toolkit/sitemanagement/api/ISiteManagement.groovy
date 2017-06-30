package gov.nist.toolkit.sitemanagement.api

import gov.nist.toolkit.sitemanagement.Sites

/**
 *
 */
interface ISiteManagement {
    // test-engine, SiteServiceManager
    // base on SiteServiceManager
    Sites getSites()  // implemented by simcommon/SimCache
}
