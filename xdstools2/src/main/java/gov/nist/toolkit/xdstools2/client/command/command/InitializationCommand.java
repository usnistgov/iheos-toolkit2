package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.command.CommandContext;
import gov.nist.toolkit.xdstools2.client.command.response.InitializationResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;


/**
 *
 */
public abstract class InitializationCommand extends GenericCommand<CommandContext, InitializationResponse> {
    public InitializationCommand() {
        super();
    }

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getInitialization(this);
    }
}
