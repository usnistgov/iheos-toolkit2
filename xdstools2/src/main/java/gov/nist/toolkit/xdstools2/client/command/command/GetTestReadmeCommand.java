package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestDetailsRequest;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetTestReadmeCommand extends GenericCommand<GetTestDetailsRequest, String> {
    @Override
    public void run(GetTestDetailsRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getTestReadme(var1, this);
    }
}
