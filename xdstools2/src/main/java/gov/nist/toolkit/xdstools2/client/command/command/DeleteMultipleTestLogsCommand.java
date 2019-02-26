package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteMultipleTestLogsRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GeneratePidRequest;

import java.util.List;

public abstract class DeleteMultipleTestLogsCommand extends GenericCommand<DeleteMultipleTestLogsRequest, List<TestOverviewDTO>> {
    @Override
    public void run(DeleteMultipleTestLogsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().deleteMultipleTestLogs(var1, this);
    }
}
