package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAllRequest;

import java.util.List;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class GetAllCommand extends GenericCommand<GetAllRequest,List<Result>>{
    @Override
    public void run(GetAllRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getAll(var1,this);
    }
}
