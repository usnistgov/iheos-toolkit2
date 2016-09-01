package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.command.CommandContext;

import java.util.List;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
abstract public class GetEnvironmentNamesCommand extends GenericCommand<CommandContext,List<String>>{
    public GetEnvironmentNamesCommand() {
        super();
    }

    @Override
    public void run(CommandContext var1) {
        toolkitService.getEnvironmentNames(var1, this);
    }
}
