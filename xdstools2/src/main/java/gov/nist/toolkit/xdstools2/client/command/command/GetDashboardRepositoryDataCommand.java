package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public abstract class GetDashboardRepositoryDataCommand extends GenericCommand<CommandContext,List<RepositoryStatus>>{
    @Override
    public void run(CommandContext context) {
        ClientUtils.INSTANCE.getToolkitServices().getDashboardRepositoryData(context,this);
    }
}
