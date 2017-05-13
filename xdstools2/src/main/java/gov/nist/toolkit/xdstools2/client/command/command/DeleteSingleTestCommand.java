package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSingleTestRequest;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class DeleteSingleTestCommand extends GenericCommand<DeleteSingleTestRequest,TestOverviewDTO>{
    @Override
    public void run(DeleteSingleTestRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().deleteSingleTestResult(var1,this);
    }
}
