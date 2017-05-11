package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimulatorEventRequest;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetSimulatorEventRequestCommand extends GenericCommand<GetSimulatorEventRequest,Result>{
    @Override
    public void run(GetSimulatorEventRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getSimulatorEventRequest(var1,this);
    }
}
