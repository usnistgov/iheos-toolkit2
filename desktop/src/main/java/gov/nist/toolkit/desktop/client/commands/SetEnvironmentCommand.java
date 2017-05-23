package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;
import gov.nist.toolkit.desktop.client.injection.ToolkitGinInjector;

/**
 *
 */
public class SetEnvironmentCommand extends GenericCommand<CommandContext,String> {

    @Override
    public void run(CommandContext context) {
        ToolkitGinInjector.INSTANCE.getClientUtils().getEnvironmentServices().setEnvironment(context,this);
    }

    @Override
    public void onComplete(String result) {
        // nothing happens here
    }
}
