package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

/**
 * Created by onh2 on 10/20/16.
 */
public abstract class GetTransactionLogCommand extends GenericCommand<GetTransactionRequest,String>{
    @Override
    public void run(GetTransactionRequest request) {
        XdsTools2Presenter.data().getToolkitServices().getTransactionLog(request,this);
    }
}
