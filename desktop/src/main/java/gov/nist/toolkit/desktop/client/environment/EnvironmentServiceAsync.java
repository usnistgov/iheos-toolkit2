package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.server.shared.command.CommandContext;

import java.util.List;

/**
 *
 */
public interface EnvironmentServiceAsync {
    void getCurrentEnvironment(AsyncCallback<String> callback);
    void getDefaultEnvironment(CommandContext context, AsyncCallback<String> callback);
    void setEnvironment(CommandContext context, AsyncCallback<String> callback);
    void getEnvironmentNames(CommandContext context, AsyncCallback<List<String>> callback);

    void setMesaTestSession(String name, AsyncCallback<String> callback);
    void getMesaTestSessionNames(CommandContext request, AsyncCallback<List<String>> callback);
    void addMesaTestSession(CommandContext context, AsyncCallback<Boolean> callback);
    void delMesaTestSession(CommandContext context, AsyncCallback<Boolean> callback);
    void getDefaultTestSession(CommandContext context, AsyncCallback<String> callback);
}
