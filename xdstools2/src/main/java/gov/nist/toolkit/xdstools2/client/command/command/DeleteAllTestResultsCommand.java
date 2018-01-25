package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Test;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.AllTestRequest;

import java.util.List;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class DeleteAllTestResultsCommand extends GenericCommand<AllTestRequest,List<Test>>{
    @Override
    public void run(AllTestRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().deleteAllTestResults(var1,this);
    }
}
