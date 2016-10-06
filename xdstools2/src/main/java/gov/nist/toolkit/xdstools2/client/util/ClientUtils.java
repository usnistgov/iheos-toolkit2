package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;

/**
 * Client Utilities singleton.
 */
public class ClientUtils {
    public static final ClientUtils INSTANCE=new ClientUtils();
    private ClientFactory clientFactory=GWT.create(ClientFactory.class);
    private EnvironmentState environmentState = new EnvironmentState();
    private TestSessionManager2 testSessionManager;

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
