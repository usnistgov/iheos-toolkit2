package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/3/16.
 */
public abstract class GetTimeAndDateCommand extends  GenericCommand<CommandContext,String>{
    @Override
    public void run(CommandContext var1) {
        XdsTools2Presenter.data().getToolkitServices().getTimeAndDate(var1,this);
    }
}