package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRawLogsRequest;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetRawLogsCommand extends GenericCommand<GetRawLogsRequest,TestLogs>{
    @Override
    public void run(GetRawLogsRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().getRawLogs(var1,this);
    }
}
