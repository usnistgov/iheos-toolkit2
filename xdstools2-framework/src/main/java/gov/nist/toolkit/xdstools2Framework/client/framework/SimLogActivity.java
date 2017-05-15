package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 *
 */
public class SimLogActivity  extends AbstractActivity {
//    private XdsTools2AppView xdstools2view = XdsTools2AppView.getInstance();
//    private SimLog simLog;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
//        if (simLog != null && simLog.isValid()) {
////            XdsTools2AppView.getInstance().doNotDisplayHomeTab();
//
//            ToolWindow toolWindow = new ToolLauncher(ToolLauncher.simulatorMessageViewTabLabel).launch();
//
//            SimId simId = new SimId(simLog.getSimIdString());
//            SimulatorMessageViewTab simulatorMessageViewTab = (SimulatorMessageViewTab) toolWindow;
//            simulatorMessageViewTab.setSimId(simId);
//            simulatorMessageViewTab.setActor(simLog.getActor());
//            simulatorMessageViewTab.setTransaction(simLog.getTrans());
//            simulatorMessageViewTab.loadTransactionNames(simId);
//            simulatorMessageViewTab.transactionChosen(simId, simLog.getTrans());
//            TransactionInstance transactionInstance = new TransactionInstance();
//            transactionInstance.actorType = ActorType.findActor(simLog.getActor());
//            transactionInstance.simId = simId.toString();
//            transactionInstance.trans = simLog.getTrans();
//            transactionInstance.messageId = simLog.getMessageId();
//            simulatorMessageViewTab.loadTransactionInstanceDetails(transactionInstance);
////            xdstools2view.resizeToolkit();
//            simulatorMessageViewTab.selectByMessageId(simLog.getMessageId());
//        }
    }

//    public void setSimLog(SimLog simLog) {
//        this.simLog = simLog;
//    }

    public XdsTools2AppView getView() { return null; }
}
