package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.IdsOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildIdsTestOrchestrationRequest extends CommandContext{
    private IdsOrchestrationRequest idsOrchestrationRequest;

    public BuildIdsTestOrchestrationRequest(){}
    public BuildIdsTestOrchestrationRequest(CommandContext context, IdsOrchestrationRequest idsOrchestrationRequest){
        copyFrom(context);
        this.idsOrchestrationRequest=idsOrchestrationRequest;
    }

    public IdsOrchestrationRequest getIdsOrchestrationRequest() {
        return idsOrchestrationRequest;
    }

    public void setIdsOrchestrationRequest(IdsOrchestrationRequest idsOrchestrationRequest) {
        this.idsOrchestrationRequest = idsOrchestrationRequest;
    }
}
