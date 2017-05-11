package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetNewSimulatorRequest;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class GetNewSimulatorCommand extends GenericCommand<GetNewSimulatorRequest,Simulator>{
    @Override
    public void run(GetNewSimulatorRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getNewSimulator(var1,this);
    }
}
