package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
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
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.Map;

/**
 *
 */
public class ConfActorActivity extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    private ConfActor confActor;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        // Can we reuse the ClientUtils copy of Tkprops instead of this new command?
        new GetToolkitPropertiesCommand() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Delete error getting properties : " + throwable.toString());
            }

            @Override
            public void onComplete(final Map<String, String> tkPropMap) {

                boolean multiUserModeEnabled = Boolean.parseBoolean(tkPropMap.get("Multiuser_mode"));
                boolean casModeEnabled = Boolean.parseBoolean(tkPropMap.get("Cas_mode"));
                ToolkitUserMode userMode = (multiUserModeEnabled)?(casModeEnabled?ToolkitUserMode.CAS_USER:ToolkitUserMode.MULTI_USER):ToolkitUserMode.SINGLE_USER;

                if (confActor != null) {
                    Xdstools2.getInstance().doNotDisplayHomeTab();

                    // Override start-up initialization of environment
                    ClientUtils.INSTANCE.getEnvironmentState().initEnvironmentName(confActor.getEnvironmentName());

                    ToolWindow toolWindow = new ToolLauncher(ToolLauncher.conformanceTestsLabel).launch();
                    ConformanceTestTab conformanceTestTab = (ConformanceTestTab) toolWindow;
                    String testSession = confActor.getTestSessionName();

                    if ("default".equalsIgnoreCase(testSession)) {
                     if (!multiUserModeEnabled) {
                         toolWindow.setCurrentTestSession(testSession);
                         conformanceTestTab.setInitTestSession(testSession);
                     } else { // CAS
                         toolWindow.setCurrentTestSession(null);
                         new PopupMessage("ConfActorActivity: Test session default cannot be selected in " + userMode);
                     }
                    } else {
                        toolWindow.setCurrentTestSession(testSession);
                        conformanceTestTab.setInitTestSession(testSession);
                        if (testSession!=null && !"".equals(testSession)) {
                            ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(testSession);
                            ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, testSession));
                        }
                    }
                    GWT.log("Launch ConformanceTool for " +
                                    "/testsession=" + confActor.getTestSessionName() +
                    "/actor=" + confActor.getActorType() +
                    "/profile=" + confActor.getProfileId() +
                    "/option=" + confActor.getOptionId() +
                            "/site=" + new SiteSpec(confActor.getSystemName(), new TestSession(confActor.getTestSessionName())) +
                            ""
                    );
                    conformanceTestTab.getCurrentActorOption().setActorTypeId(confActor.getActorType());
                    conformanceTestTab.getCurrentActorOption().setProfileId(IheItiProfile.find(confActor.getProfileId()));
                    conformanceTestTab.getCurrentActorOption().setOptionId(confActor.getOptionId());
                    conformanceTestTab.setCommonSiteSpec(new SiteSpec(confActor.getSystemName(), new TestSession(confActor.getTestSessionName())));
                    conformanceTestTab.setSiteToIssueTestAgainst(new SiteSpec(confActor.getSystemName(), new TestSession(confActor.getTestSessionName())));
                    xdstools2view.resizeToolkit();
                }

            }
        }.run(ClientUtils.INSTANCE.getCommandContext());


    }

    public void setConfActor(ConfActor confActor) { this.confActor = confActor; }

    public Xdstools2 getView() { return xdstools2view; }
}
