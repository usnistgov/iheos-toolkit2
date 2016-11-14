package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIdsTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildIdsTestOrchestrationCommand extends GenericCommand<BuildIdsTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildIdsTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildIdsTestOrchestration(var1,this);
    }
}
