package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.Map;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class GetToolkitPropertiesCommand extends GenericCommand<CommandContext,Map<String,String>>{
    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getToolkitProperties(var1,this);
    }
}
