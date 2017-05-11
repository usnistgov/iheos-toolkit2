package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIigTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildIIGTestOrchestrationCommand extends GenericCommand<BuildIigTestOrchestrationRequest,RawResponse>{

    @Override
    public void run(BuildIigTestOrchestrationRequest var1) {
        FrameworkInitialization.data().getToolkitServices().buildIigTestOrchestration(var1,this);
    }
}
