package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRgTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildRGTestOrchestrationCommand extends GenericCommand<BuildRgTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildRgTestOrchestrationRequest var1) {
        FrameworkInitialization.data().getToolkitServices().buildRgTestOrchestration(var1,this);
    }
}
