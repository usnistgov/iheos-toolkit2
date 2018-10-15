package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIsrTestOrchestrationRequest;

/**
 *
 */
public abstract class BuildIsrTestOrchestrationCommand extends GenericCommand<BuildIsrTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildIsrTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildIsrTestOrchestration(var1,this);
    }
}
