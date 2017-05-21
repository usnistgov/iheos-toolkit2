package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;

import java.util.List;


/**
 *
 */
public abstract class GetDefaultTestSessionCommand extends GenericCommand<CommandContext, String> {

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getEnvironmentServices().getDefaultTestSession(var1, this);
    }
}
