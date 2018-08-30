package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.actortransaction.shared.IheItiProfile;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.installation.shared.ToolkitUserMode;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.GetToolkitPropertiesCommand;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.ConformanceTestTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.State;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.Map;

/**
 *
 */
public class ConfActorActivity extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    private ConfActor confActor;
    State state;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        if (confActor != null) {
            Xdstools2.getInstance().doNotDisplayHomeTab();

            new GetToolkitPropertiesCommand() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("BuildTabsWrapper error getting properties : " + throwable.toString());
                }


                @Override
                public void onComplete(final Map<String, String> tkPropMap) {
                    ClientUtils.INSTANCE.setTkPropMap(tkPropMap);
                    ToolLauncher toolLauncher = new ToolLauncher(ToolLauncher.conformanceTestsLabel);
                    toolLauncher.setState(state);
                    toolLauncher.launch();

                    xdstools2view.resizeToolkit();
                }
            }.run(ClientUtils.INSTANCE.getCommandContext());

        }
    }

    public void setConfActor(ConfActor confActor) { this.confActor = confActor; }

    public Xdstools2 getView() { return xdstools2view; }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
