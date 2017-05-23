package gov.nist.toolkit.desktop.client.commands;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;

import java.util.List;

/**
 * This command retrieves a list of favorite PIDs from the server.
 * Created by onh2 on 10/4/16.
 */
public abstract class RetrieveFavPidsCommand extends GenericCommand<CommandContext, List<Pid>> {
    /**
     * This method runs the server request.
     * @param commandContext context of the call (must contain environment name).
     */
    @Override
    public void run(CommandContext commandContext) {
        ClientUtils.INSTANCE.getToolkitServices().retrieveConfiguredFavoritesPid(commandContext,this);
    }

}
