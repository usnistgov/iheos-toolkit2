package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/14/16.
 */
public abstract class AddMesaTestSessionCommand extends GenericCommand<CommandContext,Boolean>{
    @Override
    public void run(CommandContext context) {
        ClientUtils.INSTANCE.getToolkitServices().addMesaTestSession(context,this);
    }
}
