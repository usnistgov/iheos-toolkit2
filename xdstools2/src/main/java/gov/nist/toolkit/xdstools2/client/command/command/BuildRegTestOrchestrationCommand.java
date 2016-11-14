package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRegTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildRegTestOrchestrationCommand extends GenericCommand<BuildRegTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildRegTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildRegTestOrchestration(var1,this);
    }
}
