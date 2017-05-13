package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import java.util.List;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetTransactionInstancesCommand extends GenericCommand<GetTransactionRequest,List<TransactionInstance>>{
    @Override
    public void run(GetTransactionRequest request) {
        XdsTools2Presenter.data().getToolkitServices().getTransInstances(request,this);
    }
}
