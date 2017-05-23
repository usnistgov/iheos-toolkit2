package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.services.client.RgOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildRgTestOrchestrationRequest extends CommandContext {
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
