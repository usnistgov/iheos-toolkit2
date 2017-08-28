package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Gets transaction instances for all simIds
 * Created by skb1 on 08/11/17.
 */
public class GetTransactionListsRequest extends CommandContext{
    private List<SimId> simIds;

    public GetTransactionListsRequest(){}
    public GetTransactionListsRequest(CommandContext commandContext, List<SimId> simIds){
        copyFrom(commandContext);
        this.simIds=simIds;
    }


    public List<SimId> getSimIds() {
        return simIds;
    }

    public void setSimIds(List<SimId> simIds) {
        this.simIds = simIds;
    }
}
