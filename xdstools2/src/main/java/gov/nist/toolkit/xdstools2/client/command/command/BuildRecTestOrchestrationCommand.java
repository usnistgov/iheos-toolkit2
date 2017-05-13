package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRecTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildRecTestOrchestrationCommand extends GenericCommand<BuildRecTestOrchestrationRequest,RawResponse>{
    @Override
    public void run(BuildRecTestOrchestrationRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().buildRecTestOrchestration(var1,this);
    }
}
