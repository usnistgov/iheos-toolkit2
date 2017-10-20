package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRecTestOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildSrcTestOrchestrationRequest;

/**
 *
 */
public abstract class BuildSrcTestOrchestrationCommand extends GenericCommand<BuildSrcTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildSrcTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildSrcTestOrchestration(var1,this);
    }
}
