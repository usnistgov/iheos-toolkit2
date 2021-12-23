package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public abstract class ReloadToolkitLoggingPropertiesCommand extends GenericCommand<CommandContext, Boolean>{
    @Override
    public void run(CommandContext commandContext) {
        ClientUtils.INSTANCE.getToolkitServices().reloadToolkitLogging(commandContext, this);
    }
}
