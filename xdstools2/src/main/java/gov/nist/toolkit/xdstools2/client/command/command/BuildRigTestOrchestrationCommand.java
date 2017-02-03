package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRigTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildRigTestOrchestrationCommand extends GenericCommand<BuildRigTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildRigTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildRigTestOrchestration(var1,this);
    }
}
