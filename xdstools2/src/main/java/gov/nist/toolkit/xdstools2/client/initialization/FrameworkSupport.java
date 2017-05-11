package gov.nist.toolkit.xdstools2.client.initialization;

import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.tabs.QueryState;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Things that are different between Xdstools2 and Desktop - the two frameworks
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

    void resizeToolkit();

    Xdstools2EventBus getEventBus();

    TkProps getTkProps();

    QueryState getQueryState();

    EnvironmentState getEnvironmentState();

    GenericQueryTab getHomeTab();

    CommandContext getCommandContext();

    ToolkitServiceAsync getToolkitServices();


}
