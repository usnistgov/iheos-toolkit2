package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;

import java.util.List;


/**
 *
 */
public abstract class GetTestSessionNamesCommand extends GenericCommand<CommandContext, List<String>> {

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getEnvironmentServices().getMesaTestSessionNames(var1, this);
    }
}
