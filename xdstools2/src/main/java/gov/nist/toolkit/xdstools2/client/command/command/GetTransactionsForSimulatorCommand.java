package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import java.util.List;

/**
 * Created by onh2 on 10/21/16.
 */
public abstract class GetTransactionsForSimulatorCommand extends GenericCommand<GetTransactionRequest,List<String>>{
    @Override
    public void run(GetTransactionRequest request) {
        FrameworkInitialization.data().getToolkitServices().getTransactionsForSimulator(request,this);
    }
}
