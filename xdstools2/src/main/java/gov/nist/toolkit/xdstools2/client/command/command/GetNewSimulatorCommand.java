package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetNewSimulatorRequest;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class GetNewSimulatorCommand extends GenericCommand<GetNewSimulatorRequest,Simulator>{
    @Override
    public void run(GetNewSimulatorRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getNewSimulator(var1,this);
    }
}
