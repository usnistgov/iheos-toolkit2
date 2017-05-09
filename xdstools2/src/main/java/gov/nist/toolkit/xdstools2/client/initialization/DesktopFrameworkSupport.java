package gov.nist.toolkit.xdstools2.client.initialization;

import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;

/**
 * Desktop version
 */
public class DesktopFrameworkSupport implements FrameworkSupport {
    private String toolkitName;
    private String toolkitBaseUrl;
    private String wikiBaseUrl;

    @Override
    public void buildTabsWrapper() {
        // nothing needed
    }

    @Override
    public void setToolkitName(String name) {
        this.toolkitName = name;
    }

    @Override
    public String getToolkitName() {
        return toolkitName;
    }

    @Override
    public void setToolkitBaseUrl(String url) {
        this.toolkitBaseUrl = url;
    }

    @Override
    public String getToolkitBaseUrl() {
        return toolkitBaseUrl;
    }

    @Override
    public void setWikiBaseUrl(String url) {
        this.wikiBaseUrl = url;
    }

    @Override
    public String getWikiBaseUrl() {
        return wikiBaseUrl;
    }

    @Override
    public void displayHomeTab() {

    }

    @Override
    public void reloadTransactionOfferings() {
        // TODO: this needs management by eventbus
    }

    @Override
    public TestSessionManager2 getTestSessionManager() {
        // TODO:  need desktop version of getTestSessionManager
        return null;
    }
}
