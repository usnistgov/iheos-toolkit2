package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.services.client.RepOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRepTestOrchestrationRequest extends CommandContext {
    private RepOrchestrationRequest repOrchestrationRequest;

    public BuildRepTestOrchestrationRequest(){}
    public BuildRepTestOrchestrationRequest(CommandContext context, RepOrchestrationRequest repOrchestrationRequest){
        copyFrom(context);
        this.repOrchestrationRequest=repOrchestrationRequest;
    }

    public RepOrchestrationRequest getRepOrchestrationRequest() {
        return repOrchestrationRequest;
    }

    public void setRepOrchestrationRequest(RepOrchestrationRequest repOrchestrationRequest) {
        this.repOrchestrationRequest = repOrchestrationRequest;
    }
}
