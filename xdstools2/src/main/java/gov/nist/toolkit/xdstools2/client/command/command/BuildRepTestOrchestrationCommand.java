package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRepTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildRepTestOrchestrationCommand extends GenericCommand<BuildRepTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildRepTestOrchestrationRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().buildRepTestOrchestration(var1,this);
    }
}
