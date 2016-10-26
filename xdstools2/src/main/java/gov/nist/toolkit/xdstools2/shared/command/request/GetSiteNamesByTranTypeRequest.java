package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/19/16.
 */
public class GetSiteNamesByTranTypeRequest extends CommandContext{
    private String transactionTypeName;

    public GetSiteNamesByTranTypeRequest(){}
    public GetSiteNamesByTranTypeRequest(CommandContext commandContext, String transactionTypeName) {
        copyFrom(commandContext);
        this.transactionTypeName=transactionTypeName;
    }

    public String getTransactionTypeName() {
        return transactionTypeName;
    }

    public void setTransactionTypeName(String transactionTypeName) {
        this.transactionTypeName = transactionTypeName;
    }
}
