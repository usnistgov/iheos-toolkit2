package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRelatedRequest;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class GetRelatedCommand extends GenericCommand<GetRelatedRequest,List<Result>>{
    @Override
    public void run(GetRelatedRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getRelated(var1,this);
    }
}
