package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.RgOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRgTestOrchestrationRequest extends CommandContext{
    private RgOrchestrationRequest rgOrchestrationRequest;

    public BuildRgTestOrchestrationRequest(){}
    public BuildRgTestOrchestrationRequest(CommandContext context, RgOrchestrationRequest rgOrchestrationRequest){
        copyFrom(context);
        this.rgOrchestrationRequest=rgOrchestrationRequest;
    }

    public RgOrchestrationRequest getRgOrchestrationRequest() {
        return rgOrchestrationRequest;
    }

    public void setRgOrchestrationRequest(RgOrchestrationRequest rgOrchestrationRequest) {
        this.rgOrchestrationRequest = rgOrchestrationRequest;
    }
}
