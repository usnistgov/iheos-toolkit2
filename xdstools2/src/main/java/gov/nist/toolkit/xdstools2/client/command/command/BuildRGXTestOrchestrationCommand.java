package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRgxTestOrchestrationRequest;

/**
 * Cloned by Steve Moore (WUSTL) 2022.07.01.
 * Taken from BuildRGTestOrchestrationCommand
 */
public abstract class BuildRGXTestOrchestrationCommand extends GenericCommand<BuildRgxTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildRgxTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildRgxTestOrchestration(var1,this);
    }
}
