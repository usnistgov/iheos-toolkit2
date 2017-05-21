package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.environment.*;
import gov.nist.toolkit.desktop.client.injection.ToolkitGinInjector;
import gov.nist.toolkit.desktop.client.legacy.QueryState;

import javax.inject.Inject;

/**
 * Client Utilities singleton.
 */
public class ClientUtils {
    public static final ClientUtils INSTANCE = new ClientUtils();  // I hope this is initialized early enough

    // production version
//    public EnvironmentServiceAsync environmentService = GWT.create(EnvironmentService.class);
    // UI testing version
    private EnvironmentServiceAsync environmentService = GWT.create(LocalEnvironmentServiceAsync.class);

    private ToolkitServiceAsync toolkitService = GWT.create(ToolkitServiceAsync.class);

    private QueryState queryState = new QueryState();

    public QueryState getQueryState() { return queryState; }

    public ClientUtils() {
        GWT.log("ClientUtils created");
    }

    public EnvironmentServiceAsync getEnvironmentServices() {
        return environmentService;
    }

    public ToolkitServiceAsync getToolkitServices() { return toolkitService; }

    public CommandContext getCurrentCommandContext() {
        EnvironmentMVP environmentMVP = ToolkitGinInjector.INSTANCE.getEnvironmentMVP();
        TestSessionMVP testSessionMVP = ToolkitGinInjector.INSTANCE.getTestSessionMVP();

        assert(environmentMVP != null);
        assert(testSessionMVP != null);

        return new CommandContext(environmentMVP.getPresenter().getEnvironmentName(), testSessionMVP.getPresenter().getTestSessionName());
    }

//    public CommandContext getCurrentCommandContext(){
//        // this is a horrible hack until the initialization is cleaned up
//        String env = "default";
//        String testSession = "default";
//        return new CommandContext(env, testSession);
//    }
}
