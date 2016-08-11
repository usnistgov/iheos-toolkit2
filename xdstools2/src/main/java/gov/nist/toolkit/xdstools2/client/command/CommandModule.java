package gov.nist.toolkit.xdstools2.client.command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

/**
 *
 */
abstract class CommandModule<C> implements AsyncCallback<C> {
    ToolWindow toolWindow;
    ToolkitServiceAsync toolkitService;

    abstract public void onComplete(C var1);

    public CommandModule(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        toolkitService = toolWindow.toolkitService;
    }

    public ToolkitServiceAsync getToolkitService() {
        return toolkitService;
    }

    // This may be further overridden by Command Class (class that extends GenericCommand)
    @Override
     public void onFailure(Throwable throwable) {
        new PopupMessage("Request to server failed: " + throwable.getMessage());
    }

    @Override
    public void onSuccess(C var1) {
        onComplete(var1);
    }

}
