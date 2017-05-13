package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.xdstools2Framework.client.framework.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;

/**
 * Client Utilities singleton.
 */
public class ClientUtils {
    public static final ClientUtils INSTANCE=new ClientUtils();
    private ClientFactory clientFactory=GWT.create(ClientFactory.class);
    private EnvironmentState environmentState = new EnvironmentState();
    private TestSessionManager2 testSessionManager;

    // Private constructor whose sole is to hide the implicit public one and really have a Singleton.
    private ClientUtils(){}

    public ToolkitServiceAsync getToolkitServices(){
        return clientFactory.getToolkitServices();
    }

    public EventBus getEventBus(){
        return clientFactory.getEventBus();
    }

    public EnvironmentState getEnvironmentState() { return environmentState; }

    public TestSessionManager2 getTestSessionManager() {
        if (testSessionManager == null)
            testSessionManager = new TestSessionManager2();
        return testSessionManager;
    }


}
