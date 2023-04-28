package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.RgxOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Cloned by Steve Moore (WUSTL) 2022.07.01.
 * Taken from BuildIgTestOrchestrationRequest
 */
public class BuildRgxTestOrchestrationRequest extends CommandContext{
    private RgxOrchestrationRequest rgxOrchestrationRequest;

    public BuildRgxTestOrchestrationRequest(){}
    public BuildRgxTestOrchestrationRequest(CommandContext context, RgxOrchestrationRequest rgxOrchestrationRequest){
        copyFrom(context);
        this.rgxOrchestrationRequest=rgxOrchestrationRequest;
    }

    public RgxOrchestrationRequest getRgxOrchestrationRequest() {
        return rgxOrchestrationRequest;
    }

    public void setRgxOrchestrationRequest(RgxOrchestrationRequest rgxOrchestrationRequest) {
        this.rgxOrchestrationRequest = rgxOrchestrationRequest;
    }
}
