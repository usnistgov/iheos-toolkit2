package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionLogDirectoryPathRequest;

/**
 * Created by skb1 on 3/13/17.
 */
public abstract class GetTransactionLogDirectoryPathCommand extends GenericCommand<GetTransactionLogDirectoryPathRequest,TransactionInstance>{
    @Override
    public void run(GetTransactionLogDirectoryPathRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getTransactionLogDirectoryPath(request,this);
    }
}
