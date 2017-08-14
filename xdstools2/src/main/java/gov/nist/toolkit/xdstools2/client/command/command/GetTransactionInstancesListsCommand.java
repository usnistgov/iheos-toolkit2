package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionListsRequest;

import java.util.List;

/**
 * Created by skb1 on 08/11/17.
 */
public abstract class GetTransactionInstancesListsCommand extends GenericCommand<GetTransactionListsRequest,List<List<TransactionInstance>>>{
    @Override
    public void run(GetTransactionListsRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getTransInstancesLists(request,this);
    }
}
