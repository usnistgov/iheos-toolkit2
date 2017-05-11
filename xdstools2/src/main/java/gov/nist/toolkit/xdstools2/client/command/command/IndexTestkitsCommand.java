package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * This is the Command class to request the server to (re)index the testkits (Build collections).
 */
public abstract class IndexTestkitsCommand extends GenericCommand<CommandContext, Boolean> {

    @Override
    public void run(CommandContext commandContext) {
        FrameworkInitialization.data().getToolkitServices().indexTestKits(commandContext,this);
    }

}
