package gov.nist.toolkit.desktop.client.commands.util;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.desktop.client.DesktopApp;

/**
 *
 */
public abstract class CommandModule<C> implements AsyncCallback<C> {


    CommandModule() { }

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
        DesktopApp.alert(new HTML("Request to server failed: " + msg));
    }

    @Override
    public void onSuccess(C result) {
        onComplete(result);
    }

}
