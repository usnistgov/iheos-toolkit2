package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actortransaction.shared.TransactionInstance;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import java.util.List;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetTransactionInstancesCommand extends GenericCommand<GetTransactionRequest,List<TransactionInstance>>{
    @Override
    public void run(GetTransactionRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getTransInstances(request,this);
    }
}
