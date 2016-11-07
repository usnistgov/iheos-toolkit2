package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionErrorCodeRefsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetTransactionErrorCodeRefsCommand extends GenericCommand<GetTransactionErrorCodeRefsRequest,List<String>>{
    @Override
    public void run(GetTransactionErrorCodeRefsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getTransactionErrorCodeRefs(var1,this);
    }
}
