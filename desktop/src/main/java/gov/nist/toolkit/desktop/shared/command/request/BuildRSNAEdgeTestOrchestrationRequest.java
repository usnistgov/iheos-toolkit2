package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.services.client.RSNAEdgeOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRSNAEdgeTestOrchestrationRequest extends CommandContext {
    private RSNAEdgeOrchestrationRequest rsnaEdgeOrchestrationRequest;

    public BuildRSNAEdgeTestOrchestrationRequest(){}
    public BuildRSNAEdgeTestOrchestrationRequest(CommandContext context, RSNAEdgeOrchestrationRequest rsnaEdgeOrchestrationRequest){
        copyFrom(context);
        this.rsnaEdgeOrchestrationRequest=rsnaEdgeOrchestrationRequest;
    }

    public RSNAEdgeOrchestrationRequest getRsnaEdgeOrchestrationRequest() {
        return rsnaEdgeOrchestrationRequest;
    }

    public void setRsnaEdgeOrchestrationRequest(RSNAEdgeOrchestrationRequest rsnaEdgeOrchestrationRequest) {
        this.rsnaEdgeOrchestrationRequest = rsnaEdgeOrchestrationRequest;
    }
}
