package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.services.client.RecOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRecTestOrchestrationRequest extends CommandContext {
    private RecOrchestrationRequest recOrchestrationRequest;

    public BuildRecTestOrchestrationRequest(){}
    public BuildRecTestOrchestrationRequest(CommandContext context, RecOrchestrationRequest recOrchestrationRequest){
        copyFrom(context);
        this.recOrchestrationRequest=recOrchestrationRequest;
    }

    public RecOrchestrationRequest getRecOrchestrationRequest() {
        return recOrchestrationRequest;
    }

    public void setRecOrchestrationRequest(RecOrchestrationRequest recOrchestrationRequest) {
        this.recOrchestrationRequest = recOrchestrationRequest;
    }
}
