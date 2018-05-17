package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.TestSessionStats;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestSessionStatsRequest;

import java.util.List;

public abstract class GetTestSessionStatsCommand extends GenericCommand<GetTestSessionStatsRequest, List<TestSessionStats>> {

    @Override
    public void run(GetTestSessionStatsRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getTestSessionStats(request, this);
    }
}
