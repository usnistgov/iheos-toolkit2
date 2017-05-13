package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/19/16.
 */
public class SetEnvironmentCommand extends GenericCommand<CommandContext,String>{
    @Override
    public void run(CommandContext context) {
        XdsTools2Presenter.data().getToolkitServices().setEnvironment(context,this);
    }

    @Override
    public void onComplete(String result) {
        // nothing happens here
    }
}
