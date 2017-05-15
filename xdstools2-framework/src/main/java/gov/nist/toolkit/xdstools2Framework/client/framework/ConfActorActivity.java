package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 *
 */
public class ConfActorActivity extends AbstractActivity {
//    private XdsTools2AppView xdstools2view = XdsTools2AppView.getInstance();
//    private ConfActor confActor;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
//        if (confActor != null) {
////            XdsTools2AppView.getInstance().doNotDisplayHomeTab();
//
//            // Override start-up initialization of environment
////            ClientUtils.INSTANCE.getEnvironmentState().initEnvironmentName(confActor.getEnvironmentName());
//
//            ToolWindow toolWindow = new ToolLauncher(ToolLauncher.conformanceTestsLabel).launch();
//            toolWindow.setCurrentTestSession(confActor.getTestSessionName());
//            ConformanceTestTab conformanceTestTab = (ConformanceTestTab) toolWindow;
//            conformanceTestTab.setInitTestSession(confActor.getTestSessionName());
//            conformanceTestTab.changeDisplayedActorAndOptionType(new ActorOption(confActor.getActorType()));
////            xdstools2view.resizeToolkit();
//        }
    }

//    public void setConfActor(ConfActor confActor) { this.confActor = confActor; }

    public XdsTools2AppView getView() { return null; }
}
