package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;
import gov.nist.toolkit.server.shared.command.InitializationResponse;

/**
 *
 */
public abstract class InitializationCommand extends GenericCommand<CommandContext, InitializationResponse> {
    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getInitialization(var1,this);
    }
}
