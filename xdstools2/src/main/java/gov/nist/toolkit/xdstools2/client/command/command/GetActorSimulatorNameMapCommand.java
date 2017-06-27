package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 10/20/16.
 */
public abstract class GetActorSimulatorNameMapCommand extends GenericCommand<CommandContext,List<String>> {
    @Override
    public void run(CommandContext context) {
        ClientUtils.INSTANCE.getToolkitServices().getActorSimulatorNameMap(context,this);
    }
}
