package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetCodesConfiguration extends GenericCommand<CommandContext,CodesResult>{
    @Override
    public void run(CommandContext var1) {
        XdsTools2Presenter.data().getToolkitServices().getCodesConfiguration(var1,this);
    }
}
