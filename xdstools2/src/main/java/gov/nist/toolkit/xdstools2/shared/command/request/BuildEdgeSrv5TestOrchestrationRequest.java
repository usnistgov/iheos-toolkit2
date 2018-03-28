package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.EdgeSrv5OrchestrationRequest;
import gov.nist.toolkit.services.client.RSNAEdgeOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildEdgeSrv5TestOrchestrationRequest extends CommandContext{
    private EdgeSrv5OrchestrationRequest request;

    public BuildEdgeSrv5TestOrchestrationRequest(){}
    public BuildEdgeSrv5TestOrchestrationRequest(CommandContext context, EdgeSrv5OrchestrationRequest request){
        copyFrom(context);
        this.request = request;
    }

    public EdgeSrv5OrchestrationRequest getEdgeSrv5OrchestrationRequest() {
        return request;
    }

    public void setEdgeSrv5OrchestrationRequest(EdgeSrv5OrchestrationRequest request) {
        this.request = request;
    }
}
