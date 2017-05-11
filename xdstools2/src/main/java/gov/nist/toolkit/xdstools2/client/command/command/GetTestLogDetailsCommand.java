package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestLogDetailsRequest;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class GetTestLogDetailsCommand extends GenericCommand<GetTestLogDetailsRequest,LogFileContentDTO>{
    @Override
    public void run(GetTestLogDetailsRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getTestLogDetails(var1,this);
    }
}
