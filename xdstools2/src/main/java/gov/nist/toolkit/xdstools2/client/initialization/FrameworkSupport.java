package gov.nist.toolkit.xdstools2.client.initialization;

import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;

/**
 * Things that are different between Xdstools2 and Desktop
 */
public interface FrameworkSupport {
    void buildTabsWrapper();

    void setToolkitName(String name);
    String getToolkitName();

    void setToolkitBaseUrl(String url);
    String getToolkitBaseUrl();

    void setWikiBaseUrl(String url);
    String getWikiBaseUrl();

    void displayHomeTab();

    void reloadTransactionOfferings();

    TestSessionManager2 getTestSessionManager();
}
