package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.EsOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public class BuildEsTestOrchestrationRequest extends CommandContext {
    private EsOrchestrationRequest esOrchestrationRequest;

    public BuildEsTestOrchestrationRequest() {}
    public BuildEsTestOrchestrationRequest(CommandContext context,EsOrchestrationRequest esOrchestrationRequest) {
        copyFrom(context);
        this.esOrchestrationRequest = esOrchestrationRequest;
    }

    public EsOrchestrationRequest getEsOrchestrationRequest() {
        return esOrchestrationRequest;
    }

    public void setEsOrchestrationRequest(EsOrchestrationRequest esOrchestrationRequest) {
        this.esOrchestrationRequest = esOrchestrationRequest;
    }
}
