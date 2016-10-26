package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

/**
 * Created by onh2 on 10/20/16.
 */
public abstract class GetTransactionResponseCommand extends GenericCommand<GetTransactionRequest,String>{

    @Override
    public void run(GetTransactionRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getTransactionResponse(request,this);
    }
}
