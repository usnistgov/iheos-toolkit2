package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.IdcxcaOrchestrationRequest;
import gov.nist.toolkit.services.client.IigOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildIdcxcaTestOrchestrationRequest extends CommandContext{
    private IdcxcaOrchestrationRequest idcxcaOrchestrationRequest;

    public BuildIdcxcaTestOrchestrationRequest(){}
    public BuildIdcxcaTestOrchestrationRequest(CommandContext context, IdcxcaOrchestrationRequest idcxcaOrchestrationRequest){
        copyFrom(context);
        this.idcxcaOrchestrationRequest = idcxcaOrchestrationRequest;
    }

    public IdcxcaOrchestrationRequest getIdcxcaOrchestrationRequest() {
        return idcxcaOrchestrationRequest;
    }

    public void setIdcxcaOrchestrationRequest(IdcxcaOrchestrationRequest idcxcaOrchestrationRequest) {
        this.idcxcaOrchestrationRequest = idcxcaOrchestrationRequest;
    }
}
