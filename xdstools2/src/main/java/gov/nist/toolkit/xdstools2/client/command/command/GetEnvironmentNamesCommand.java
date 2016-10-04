package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;

/**
 *
 */
abstract public class GetEnvironmentNamesCommand extends GenericCommand<CommandContext,List<String>>{
    public GetEnvironmentNamesCommand() {
        super();
    }

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices()
                .getEnvironmentNames(var1, this);
    }
}
