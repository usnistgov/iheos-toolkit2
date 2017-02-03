package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSubmissionSetsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class GetSubmissionSetsCommand extends GenericCommand<GetSubmissionSetsRequest,List<Result>>{
    @Override
    public void run(GetSubmissionSetsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getSubmissionSets(var1,this);
    }
}
