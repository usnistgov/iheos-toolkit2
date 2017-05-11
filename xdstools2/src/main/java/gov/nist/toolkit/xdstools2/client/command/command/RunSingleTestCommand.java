package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.RunSingleTestRequest;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class RunSingleTestCommand extends GenericCommand<RunSingleTestRequest,Test>{
    @Override
    public void run(RunSingleTestRequest var1) {
        FrameworkInitialization.data().getToolkitServices().runSingleTest(var1,this);
    }
}
