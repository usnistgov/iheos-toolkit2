package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;

import java.util.Map;

/**
 *
 */
public abstract class GetToolkitPropertiesCommand extends GenericCommand<CommandContext,Map<String,String>> {
    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getToolkitProperties(var1,this);
    }
}
