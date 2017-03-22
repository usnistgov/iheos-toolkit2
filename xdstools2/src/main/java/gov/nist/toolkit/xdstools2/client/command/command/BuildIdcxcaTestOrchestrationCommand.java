package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIdcxcaTestOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIigTestOrchestrationRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class BuildIdcxcaTestOrchestrationCommand extends GenericCommand<BuildIdcxcaTestOrchestrationRequest,RawResponse>{

    @Override
    public void run(BuildIdcxcaTestOrchestrationRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().buildIdcxcaTestOrchestration(var1,this);
    }
}
