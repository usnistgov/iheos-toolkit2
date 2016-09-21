package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.command.CommandContext;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;


/**
 *
 */
abstract public class GetTestSessionNamesCommand extends GenericCommand<CommandContext, List<String>> {
    public GetTestSessionNamesCommand() {
        super();
    }

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getMesaTestSessionNames(var1, this);
    }
}
