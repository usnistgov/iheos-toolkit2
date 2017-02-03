package gov.nist.toolkit.xdstools2.client.command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

/**
 *
 */
public abstract class CommandModule<C> implements AsyncCallback<C> {
    public CommandModule() {
    }

    /**
     * This is the method that implement the actions to be executed
     * onSucces of the AsyncCallback.
     * @param result result of the AsyncCallback.
     */
    public abstract void onComplete(C result);

    // This may be further overridden by Command Class (class that extends GenericCommand)
    @Override
    public void onFailure(Throwable throwable) {
        String msg = throwable.getMessage();
        if (msg == null)
            msg = this.getClass().getName();
        new PopupMessage("Request to server failed: " + msg);
    }

    @Override
    public void onSuccess(C result) {
        onComplete(result);
    }

}
