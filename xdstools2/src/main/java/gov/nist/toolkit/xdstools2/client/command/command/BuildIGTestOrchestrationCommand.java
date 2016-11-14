package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIgTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildIGTestOrchestrationCommand extends GenericCommand<BuildIgTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildIgTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildIgTestOrchestration(var1,this);
    }
}
