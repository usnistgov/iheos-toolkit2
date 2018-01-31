package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 1/30/18.
 */
public abstract class IsTestSessionValidCommand extends GenericCommand<CommandContext, Boolean>{
    @Override
    public void run(CommandContext request) {
        ClientUtils.INSTANCE.getToolkitServices().isTestSessionValid(request, this);
    }
}
