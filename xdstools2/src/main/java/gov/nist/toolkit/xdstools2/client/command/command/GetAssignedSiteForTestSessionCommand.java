package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/14/16.
 */
public abstract class GetAssignedSiteForTestSessionCommand extends GenericCommand<CommandContext,String>{
    @Override
    public void run(CommandContext var1) {
        FrameworkInitialization.data().getToolkitServices().getAssignedSiteForTestSession(var1,this);
    }
}
