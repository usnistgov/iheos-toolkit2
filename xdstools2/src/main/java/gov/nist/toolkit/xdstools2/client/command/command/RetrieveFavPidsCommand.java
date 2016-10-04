package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.command.CommandContext;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;

/**
 * Created by onh2 on 10/4/16.
 */
public abstract class RetrieveFavPidsCommand extends GenericCommand<CommandContext, List<Pid>> {
    @Override
    public void run(CommandContext commandContext) {
        ClientUtils.INSTANCE.getToolkitServices().retrieveConfiguredFavoritesPid(commandContext,this);
    }

}
