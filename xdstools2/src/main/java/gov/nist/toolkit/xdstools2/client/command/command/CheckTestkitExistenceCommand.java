package gov.nist.toolkit.xdstools2.client.command.command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/13/16.
 */
public abstract class CheckTestkitExistenceCommand extends GenericCommand<CommandContext,Boolean>{
    /**
     * Run the Async server call
     * {@link ToolkitServiceAsync#doesTestkitExist(CommandContext, AsyncCallback) doesTestkitExist}.
     * @param context
     */
    public void run(CommandContext context){
        FrameworkInitialization.data().getToolkitServices().doesTestkitExist(context,this);
    }
}
