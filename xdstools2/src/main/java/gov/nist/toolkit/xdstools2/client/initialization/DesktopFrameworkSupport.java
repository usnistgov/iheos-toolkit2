package gov.nist.toolkit.xdstools2.client.initialization;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2Framework.client.framework.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentStateImpl;
import gov.nist.toolkit.xdstools2.client.tabs.QueryState;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.CommandContextFactory;
import gov.nist.toolkit.xdstools2.client.util.ToolkitService;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Desktop version
 */
public class DesktopFrameworkSupport implements FrameworkSupport {
    private String toolkitName;
    private String toolkitBaseUrl;
    private String wikiBaseUrl;
    private static final ToolkitServiceAsync TOOLKIT_SERVICES = GWT.create(ToolkitService.class);
    private static final Xdstools2EventBus EVENT_BUS = new Xdstools2EventBus();

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

    @Override
    public void resizeToolkit() {
        // needed?
    }

    @Override
    public Xdstools2EventBus getEventBus() {
        return EVENT_BUS;
    }

    @Override
    public TkProps getTkProps() {
        // I don't think we need this.
        return null;
    }

    @Override
    public QueryState getQueryState() {
        // I don't think we need this.
        return null;
    }

    @Override
    public EnvironmentStateImpl getEnvironmentState() {
        // do we need this?
        return null;
    }

    @Override
    public GenericQueryTab getHomeTab() {
        // Not needed
        return null;
    }

    @Override
    public CommandContext getCommandContext() {
        return CommandContextFactory.getCommandContext();
    }

    @Override
    public ToolkitServiceAsync getToolkitServices() {
        return TOOLKIT_SERVICES;
    }
}
