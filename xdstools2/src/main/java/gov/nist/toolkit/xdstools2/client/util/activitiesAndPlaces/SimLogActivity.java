package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.simcommon.client.SimId;
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
            TransactionInstance transactionInstance = new TransactionInstance();
            transactionInstance.actorType = ActorType.findActor(simLog.getActor());
            transactionInstance.simId = simId.toString();
            transactionInstance.trans = simLog.getTrans();
            transactionInstance.messageId = simLog.getMessageId();
            simulatorMessageViewTab.loadTransactionInstanceDetails(transactionInstance);
            xdstools2view.resizeToolkit();
            simulatorMessageViewTab.selectByMessageId(simLog.getMessageId());
        }
    }

    public void setSimLog(SimLog simLog) {
        this.simLog = simLog;
    }

    public Xdstools2 getView() { return xdstools2view; }
}
