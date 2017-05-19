package gov.nist.toolkit.desktop.client.commands;

import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;

import java.util.List;

/**
 *
 */
public abstract class GetEnvironmentNamesCommand extends GenericCommand<CommandContext,List<String>> {

    private ClientUtils clientUtils = ClientUtils.INSTANCE;

    @Override
    public void run(CommandContext var1) {
        clientUtils.getEnvironmentServices()
                .getEnvironmentNames(var1, this);
    }
}
