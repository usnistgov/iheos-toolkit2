package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;
import gov.nist.toolkit.desktop.client.injection.ToolkitGinInjector;

/**
 *
 */
public class DeleteTestSessionCommand extends GenericCommand<CommandContext, Boolean> {

    @Override
    public void onComplete(Boolean result) {
        // nothing happens here
    }

    @Override
    public void run(CommandContext cc) {
        ToolkitGinInjector.INSTANCE.getClientUtils().getToolkitServices().delMesaTestSession(cc, this);

    }

}
