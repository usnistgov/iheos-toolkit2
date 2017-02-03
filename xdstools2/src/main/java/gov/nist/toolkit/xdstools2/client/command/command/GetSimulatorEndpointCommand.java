package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/1/16.
 */
public abstract class GetSimulatorEndpointCommand extends GenericCommand<CommandContext,String>{
    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getSimulatorEndpoint(var1,this);
    }
}
