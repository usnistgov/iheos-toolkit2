package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;
import gov.nist.toolkit.desktop.client.injection.ToolkitGinInjector;

/**
 *
 */
public class SetTestSessionCommand extends GenericCommand<CommandContext, String> {

    @Override
    public void onComplete(String result) {
        // nothing happens here
    }

    @Override
    public void run(CommandContext cc) {
        ToolkitGinInjector.INSTANCE.getClientUtils().getEnvironmentServices().setMesaTestSession(cc.getTestSessionName(), this);

    }

}
