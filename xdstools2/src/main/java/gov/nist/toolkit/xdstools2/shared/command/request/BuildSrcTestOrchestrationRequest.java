package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.RecOrchestrationRequest;
import gov.nist.toolkit.services.client.SrcOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class BuildSrcTestOrchestrationRequest extends CommandContext {
    private SrcOrchestrationRequest srcOrchestrationRequest;

    public BuildSrcTestOrchestrationRequest(){}
    public BuildSrcTestOrchestrationRequest(CommandContext context, SrcOrchestrationRequest srcOrchestrationRequest){
        copyFrom(context);
        this.srcOrchestrationRequest=srcOrchestrationRequest;
    }

    public SrcOrchestrationRequest getSrcOrchestrationRequest() {
        return srcOrchestrationRequest;
    }

    public void setSrcOrchestrationRequest(SrcOrchestrationRequest srcOrchestrationRequest) {
        this.srcOrchestrationRequest = srcOrchestrationRequest;
    }
}
