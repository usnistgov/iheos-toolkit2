package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.RegistryStatus;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public abstract class GetDashboardRegistryDataCommand extends GenericCommand<CommandContext,List<RegistryStatus>>{
    @Override
    public void run(CommandContext context) {
        FrameworkInitialization.data().getToolkitServices().getDashboardRegistryData(context,this);
    }
}
