package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public class BuildFhirSupportOrchestrationRequest extends CommandContext {
    private FhirSupportOrchestrationRequest fhirSupportOrchestrationRequest;

    public BuildFhirSupportOrchestrationRequest() {}
    public BuildFhirSupportOrchestrationRequest(FhirSupportOrchestrationRequest fhirSupportOrchestrationRequest) {
        this.fhirSupportOrchestrationRequest = fhirSupportOrchestrationRequest;
    }

    public FhirSupportOrchestrationRequest getFhirSupportOrchestrationRequest() {
        return fhirSupportOrchestrationRequest;
    }

    public void setFhirSupportOrchestrationRequest(FhirSupportOrchestrationRequest fhirSupportOrchestrationRequest) {
        this.fhirSupportOrchestrationRequest = fhirSupportOrchestrationRequest;
    }
}
