package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.services.client.RegOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRegTestOrchestrationRequest extends CommandContext {
    private RegOrchestrationRequest regOrchestrationRequest;

    public BuildRegTestOrchestrationRequest(){}
    public BuildRegTestOrchestrationRequest(CommandContext context, RegOrchestrationRequest regOrchestrationRequest){
        copyFrom(context);
        this.regOrchestrationRequest=regOrchestrationRequest;
    }

    public RegOrchestrationRequest getRegOrchestrationRequest() {
        return regOrchestrationRequest;
    }

    public void setRegOrchestrationRequest(RegOrchestrationRequest regOrchestrationRequest) {
        this.regOrchestrationRequest = regOrchestrationRequest;
    }
}
