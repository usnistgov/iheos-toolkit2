package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

import java.util.List;

/**
 *
 */
public interface EnvironmentServiceAsync {
    void getCurrentEnvironment(AsyncCallback<String> callback);
    void getDefaultEnvironment(CommandContext context, AsyncCallback<String> callback);
    void setEnvironment(CommandContext context, AsyncCallback<String> callback);
    void getEnvironmentNames(CommandContext context, AsyncCallback<List<String>> callback);

}
