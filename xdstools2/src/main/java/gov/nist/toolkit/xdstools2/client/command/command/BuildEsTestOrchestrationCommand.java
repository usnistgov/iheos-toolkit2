package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildEsTestOrchestrationRequest;

public abstract class BuildEsTestOrchestrationCommand extends GenericCommand<BuildEsTestOrchestrationRequest,RawResponse>{

    @Override
    public void run(BuildEsTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildEsTestOrchestration(var1,this);
    }
}
