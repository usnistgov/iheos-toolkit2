package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;


/**
 *
 */
public abstract class GetDefaultTestSessionCommand extends GenericCommand<CommandContext, String> {

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getEnvironmentServices().getDefaultTestSession(var1, this);
    }
}
