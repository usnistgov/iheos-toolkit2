package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public abstract class GetTestsOverviewCommand extends GenericCommand<GetTestsOverviewRequest,List<TestOverviewDTO>> {
    @Override
    public void run(GetTestsOverviewRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getTestsOverview(request,this);
    }
}
