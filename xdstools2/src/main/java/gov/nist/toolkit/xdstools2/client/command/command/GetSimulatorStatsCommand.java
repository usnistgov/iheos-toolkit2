package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimulatorStatsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetSimulatorStatsCommand extends GenericCommand<GetSimulatorStatsRequest,List<SimulatorStats>>{
    @Override
    public void run(GetSimulatorStatsRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getSimulatorStats(var1,this);
    }
}
