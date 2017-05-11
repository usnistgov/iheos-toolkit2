package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class RemoveOldSimulatorsCommand extends GenericCommand<CommandContext,Integer> {
    @Override
    public void run(CommandContext var1) {
        FrameworkInitialization.data().getToolkitServices().removeOldSimulators(var1,this);
    }
}
