package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

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
        XdsTools2Presenter.data().getToolkitServices().retrieveConfiguredFavoritesPid(commandContext,this);
    }

}
