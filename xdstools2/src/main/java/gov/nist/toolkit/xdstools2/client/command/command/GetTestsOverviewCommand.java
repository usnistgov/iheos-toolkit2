package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public abstract class GetTestsOverviewCommand extends GenericCommand<GetTestsOverviewRequest,List<TestOverviewDTO>> {
    @Override
    public void run(GetTestsOverviewRequest request) {
        XdsTools2Presenter.data().getToolkitServices().getTestsOverview(request,this);
    }
}
