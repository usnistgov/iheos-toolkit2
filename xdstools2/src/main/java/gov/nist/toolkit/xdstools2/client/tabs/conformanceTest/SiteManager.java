package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.sitemanagement.client.Site;

/**
 *
 */
public interface SiteManager {
    String getSiteName();
    void setSiteName(String site);
    void update();
    Site getSiteUnderTest();
}
