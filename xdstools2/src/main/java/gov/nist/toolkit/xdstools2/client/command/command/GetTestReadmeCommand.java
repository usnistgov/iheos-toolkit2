package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestDetailsRequest;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetTestReadmeCommand extends GenericCommand<GetTestDetailsRequest, String> {
    @Override
    public void run(GetTestDetailsRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().getTestReadme(var1, this);
    }
}
