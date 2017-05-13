package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestDetailsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetTestIndexCommand extends GenericCommand<GetTestDetailsRequest, List<String>> {
    @Override
    public void run(GetTestDetailsRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().getTestIndex(var1, this);
    }
}
