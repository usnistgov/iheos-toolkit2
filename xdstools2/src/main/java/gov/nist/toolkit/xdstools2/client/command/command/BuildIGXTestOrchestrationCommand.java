package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIgxTestOrchestrationRequest;

/**
 * Cloned by Steve Moore (WUSTL) 2022.07.01.
 * Taken from BuildIGTestOrchestrationCommand
 */
public abstract class BuildIGXTestOrchestrationCommand extends GenericCommand<BuildIgxTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildIgxTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildIgxTestOrchestration(var1,this);
    }
}
