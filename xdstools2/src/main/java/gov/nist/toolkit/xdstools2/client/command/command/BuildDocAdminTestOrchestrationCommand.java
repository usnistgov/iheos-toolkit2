package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildDocAdminTestOrchestrationRequest;

/**
 *
 */
public abstract class BuildDocAdminTestOrchestrationCommand extends GenericCommand<BuildDocAdminTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildDocAdminTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildDocAdminTestOrchestration(var1,this);
    }
}
