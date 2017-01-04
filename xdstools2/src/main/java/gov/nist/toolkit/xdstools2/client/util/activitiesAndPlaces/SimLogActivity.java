package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.tabs.SimulatorMessageViewTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

/**
 *
 */
public class SimLogActivity  extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    private SimLog simLog;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        Window.alert("Start SimLogActivity");
        if (simLog != null && simLog.isValid()) {
            Xdstools2.getInstance().doNotDisplayHomeTab();

            ToolWindow toolWindow = new ToolLauncher(ToolLauncher.simulatorMessageViewTabLabel).launch();

            SimId simId = new SimId(simLog.getSimIdString());
            SimulatorMessageViewTab simulatorMessageViewTab = (SimulatorMessageViewTab) toolWindow;
            simulatorMessageViewTab.setSimId(simId);
            simulatorMessageViewTab.setActor(simLog.getActor());
            simulatorMessageViewTab.setTransaction(simLog.getTrans());
            simulatorMessageViewTab.loadTransactionNames(simId);
            simulatorMessageViewTab.transactionChosen(simId, simLog.getTrans());
            xdstools2view.resizeToolkit();
        }
    }

    public void setSimLog(SimLog simLog) {
        this.simLog = simLog;
    }

    public Xdstools2 getView() { return xdstools2view; }
}
