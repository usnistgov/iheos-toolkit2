package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/19/16.
 */
public abstract class GetDefaultEnvironmentCommand extends GenericCommand<CommandContext,String>{
    @Override
    public void run(CommandContext var1) {
        FrameworkInitialization.data().getToolkitServices().getDefaultEnvironment(var1,this);
    }
}
