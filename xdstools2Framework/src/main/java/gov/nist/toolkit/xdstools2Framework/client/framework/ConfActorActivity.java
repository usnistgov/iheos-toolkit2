package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.ActorOption;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.ConformanceTestTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ConfActor;

/**
 *
 */
public class ConfActorActivity extends AbstractActivity {
    private XdsTools2AppView xdstools2view = XdsTools2AppViewImpl.getInstance();
    private ConfActor confActor;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        if (confActor != null) {
//            XdsTools2AppViewImpl.getInstance().doNotDisplayHomeTab();

            // Override start-up initialization of environment
//            ClientUtils.INSTANCE.getEnvironmentState().initEnvironmentName(confActor.getEnvironmentName());

            ToolWindow toolWindow = new ToolLauncher(ToolLauncher.conformanceTestsLabel).launch();
            toolWindow.setCurrentTestSession(confActor.getTestSessionName());
            ConformanceTestTab conformanceTestTab = (ConformanceTestTab) toolWindow;
            conformanceTestTab.setInitTestSession(confActor.getTestSessionName());
            conformanceTestTab.changeDisplayedActorAndOptionType(new ActorOption(confActor.getActorType()));
//            xdstools2view.resizeToolkit();
        }
    }

    public void setConfActor(ConfActor confActor) { this.confActor = confActor; }

    public XdsTools2AppView getView() { return xdstools2view; }
}
