package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

import java.util.List;

/**
 *
 */
public class LocalEnvironmentServiceAsync implements EnvironmentServiceAsync {
    private LocalEnvironmentServiceImpl service = new LocalEnvironmentServiceImpl();


    @Override
    public void getCurrentEnvironment(AsyncCallback<String> callback) {
        try {
            callback.onSuccess(service.getCurrentEnvironment());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void getDefaultEnvironment(CommandContext context, AsyncCallback<String> callback) {
        try {
            callback.onSuccess(service.getDefaultEnvironment(context));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void setEnvironment(CommandContext context, AsyncCallback<String> callback) {
        try {
            callback.onSuccess(service.setEnvironment(context));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void getEnvironmentNames(CommandContext context, AsyncCallback<List<String>> callback) {
        try {
            callback.onSuccess(service.getEnvironmentNames(context));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void setMesaTestSession(String name, AsyncCallback<String> callback) {
        try {
            callback.onSuccess(service.setMesaTestSession(name));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void getMesaTestSessionNames(CommandContext request, AsyncCallback<List<String>> callback) {
        try {
            callback.onSuccess(service.getMesaTestSessionNames(request));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void addMesaTestSession(CommandContext context, AsyncCallback<Boolean> callback) {
        try {
            callback.onSuccess(service.addMesaTestSession(context));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void delMesaTestSession(CommandContext context, AsyncCallback<Boolean> callback) {
        try {
            callback.onSuccess(service.delMesaTestSession(context));
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}
