package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.SubmitTestdataRequest;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class SubmitRegistryTestdataCommand extends GenericCommand<SubmitTestdataRequest,List<Result>>{
    @Override
    public void run(SubmitTestdataRequest var1) {
        FrameworkInitialization.data().getToolkitServices().submitRegistryTestdata(var1,this);
    }
}
