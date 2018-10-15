package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.IsrOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class BuildIsrTestOrchestrationRequest extends CommandContext {
    private IsrOrchestrationRequest isrOrchestrationRequest;

    public BuildIsrTestOrchestrationRequest(){}
    public BuildIsrTestOrchestrationRequest(CommandContext context, IsrOrchestrationRequest isrOrchestrationRequest){
        copyFrom(context);
        this.isrOrchestrationRequest=isrOrchestrationRequest;
    }

    public IsrOrchestrationRequest getIsrOrchestrationRequest() {
        return isrOrchestrationRequest;
    }

    public void setIsrOrchestrationRequest(IsrOrchestrationRequest isrOrchestrationRequest) {
        this.isrOrchestrationRequest = isrOrchestrationRequest;
    }
}
