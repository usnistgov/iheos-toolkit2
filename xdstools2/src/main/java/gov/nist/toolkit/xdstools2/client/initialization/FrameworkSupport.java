package gov.nist.toolkit.xdstools2.client.initialization;

import gov.nist.toolkit.xdstools2.client.tabs.QueryState;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

/**
 * Things that are different between Xdstools2 and Desktop - the two frameworks
 */
public interface FrameworkSupport {
    void setToolkitName(String name);

    String getToolkitName();

    void setToolkitBaseUrl(String url);

    String getToolkitBaseUrl();

    void setWikiBaseUrl(String url);

    String getWikiBaseUrl();

    void displayHomeTab();

    void reloadTransactionOfferings();

    void resizeToolkit();

    QueryState getQueryState();

    GenericQueryTab getHomeTab();

}
