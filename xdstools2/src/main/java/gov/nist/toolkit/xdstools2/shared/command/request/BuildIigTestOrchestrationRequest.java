package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.IigOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildIigTestOrchestrationRequest extends CommandContext{
    private IigOrchestrationRequest iigOrchestrationRequest;

    public BuildIigTestOrchestrationRequest(){}
    public BuildIigTestOrchestrationRequest(CommandContext context, IigOrchestrationRequest iigOrchestrationRequest){
        copyFrom(context);
        this.iigOrchestrationRequest=iigOrchestrationRequest;
    }

    public IigOrchestrationRequest getIigOrchestrationRequest() {
        return iigOrchestrationRequest;
    }

    public void setIigOrchestrationRequest(IigOrchestrationRequest iigOrchestrationRequest) {
        this.iigOrchestrationRequest = iigOrchestrationRequest;
    }
}
