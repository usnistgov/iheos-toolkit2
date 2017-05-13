package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public abstract class GetDashboardRepositoryDataCommand extends GenericCommand<CommandContext,List<RepositoryStatus>>{
    @Override
    public void run(CommandContext context) {
        XdsTools2Presenter.data().getToolkitServices().getDashboardRepositoryData(context,this);
    }
}
