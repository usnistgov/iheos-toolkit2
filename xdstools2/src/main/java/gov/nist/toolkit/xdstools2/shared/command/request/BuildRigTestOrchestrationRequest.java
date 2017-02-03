package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.RigOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRigTestOrchestrationRequest extends CommandContext{
    private RigOrchestrationRequest rigOrchestrationRequest;

    public BuildRigTestOrchestrationRequest(){}
    public BuildRigTestOrchestrationRequest(CommandContext context, RigOrchestrationRequest rigOrchestrationRequest){
        copyFrom(context);
        this.rigOrchestrationRequest=rigOrchestrationRequest;
    }

    public RigOrchestrationRequest getRigOrchestrationRequest() {
        return rigOrchestrationRequest;
    }

    public void setRigOrchestrationRequest(RigOrchestrationRequest rigOrchestrationRequest) {
        this.rigOrchestrationRequest = rigOrchestrationRequest;
    }
}
