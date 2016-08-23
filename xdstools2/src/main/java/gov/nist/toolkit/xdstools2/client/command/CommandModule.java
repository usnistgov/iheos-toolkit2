package gov.nist.toolkit.xdstools2.client.command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolWindow;

/**
 *
 */
abstract public class CommandModule<C> implements AsyncCallback<C> {
    ToolWindow toolWindow;

    abstract public void onComplete(C var1);

    public CommandModule(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    // This may be further overridden by Command Class (class that extends GenericCommand)
    @Override
     public void onFailure(Throwable throwable) {
        String msg = throwable.getMessage();
        if (msg == null)
            msg = this.getClass().getName();
        new PopupMessage("Request to server failed: " + msg);
    }

    @Override
    public void onSuccess(C var1) {
        onComplete(var1);
    }

}
