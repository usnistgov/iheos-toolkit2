package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

public abstract class BuildFhirSupportOrchestrationCommand extends GenericCommand<FhirSupportOrchestrationRequest,RawResponse>{
    @Override
    public void run(FhirSupportOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildFhirSupportOrchestration(var1,this);
    }
}
