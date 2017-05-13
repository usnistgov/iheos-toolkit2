package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRSNAEdgeTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildRSNAEdgeTestOrchestrationCommand extends GenericCommand<BuildRSNAEdgeTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildRSNAEdgeTestOrchestrationRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().buildRSNAEdgeTestOrchestration(var1,this);
    }
}
