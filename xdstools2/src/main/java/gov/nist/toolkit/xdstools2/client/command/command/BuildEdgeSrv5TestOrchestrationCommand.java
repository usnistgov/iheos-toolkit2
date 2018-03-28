package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildEdgeSrv5TestOrchestrationRequest;

public abstract class BuildEdgeSrv5TestOrchestrationCommand  extends GenericCommand<BuildEdgeSrv5TestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildEdgeSrv5TestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildEdgeSrv5TestOrchestration(var1,this);
    }
}
