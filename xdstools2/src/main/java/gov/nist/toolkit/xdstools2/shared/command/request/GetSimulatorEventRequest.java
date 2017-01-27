package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.actortransaction.shared.TransactionInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/7/16.
 */
public class GetSimulatorEventRequest extends CommandContext{
    private TransactionInstance transactionInstance;

    public GetSimulatorEventRequest(){}
    public GetSimulatorEventRequest(CommandContext context,TransactionInstance transactionInstance){
        copyFrom(context);
        this.transactionInstance=transactionInstance;
    }

    public TransactionInstance getTransactionInstance() {
        return transactionInstance;
    }

    public void setTransactionInstance(TransactionInstance transactionInstance) {
        this.transactionInstance = transactionInstance;
    }
}
