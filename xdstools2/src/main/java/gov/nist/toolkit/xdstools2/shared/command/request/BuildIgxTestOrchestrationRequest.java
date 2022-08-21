package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.IgxOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Cloned by Steve Moore (WUSTL) 2022.07.01.
 * Taken from BuildIgTestOrchestrationRequest
 */
public class BuildIgxTestOrchestrationRequest extends CommandContext{
    private IgxOrchestrationRequest request;

    public BuildIgxTestOrchestrationRequest(){}
    public BuildIgxTestOrchestrationRequest(CommandContext context, IgxOrchestrationRequest request){
        copyFrom(context);
        this.request=request;
    }

    public IgxOrchestrationRequest getIgxOrchestrationRequest() {
        return request;
    }

    public void setIgxOrchestrationRequest(IgxOrchestrationRequest request) {
        this.request = request;
    }
}
