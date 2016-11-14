package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.RegOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRegTestOrchestrationRequest extends CommandContext{
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
