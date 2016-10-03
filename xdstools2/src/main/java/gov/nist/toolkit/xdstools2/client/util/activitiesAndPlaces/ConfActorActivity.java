package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.ConformanceTestTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 *
 */
public class ConfActorActivity extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    private ConfActor confActor;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        if (confActor != null) {
            Xdstools2.getInstance().doNotDisplayHomeTab();

            // Override start-up initialization of environment
            ClientUtils.INSTANCE.getEnvironmentState().initEnvironmentName(confActor.getEnvironmentName());

            ToolWindow toolWindow = new ToolLauncher(ToolLauncher.conformanceTestsLabel).launch();
            toolWindow.setCurrentTestSession(confActor.getTestSessionName());
            ConformanceTestTab conformanceTestTab = (ConformanceTestTab) toolWindow;
            conformanceTestTab.setInitTestSession(confActor.getTestSessionName());
            conformanceTestTab.displayActor(confActor.getActorType());
            xdstools2view.resizeToolkit();
        }
    }

    public void setConfActor(ConfActor confActor) { this.confActor = confActor; }

    public Xdstools2 getView() { return xdstools2view; }
}
