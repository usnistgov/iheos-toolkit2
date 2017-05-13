package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.RunTestRequest;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class RunTestCommand extends GenericCommand<RunTestRequest,TestOverviewDTO>{
    @Override
    public void run(RunTestRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().runTest(var1,this);
    }
}
