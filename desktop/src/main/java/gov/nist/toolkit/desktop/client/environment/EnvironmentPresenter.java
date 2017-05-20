package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.CookieManager;
import gov.nist.toolkit.desktop.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.desktop.client.commands.GetEnvironmentNamesCommand;
import gov.nist.toolkit.desktop.client.commands.SetEnvironmentCommand;
import gov.nist.toolkit.desktop.client.events.EnvironmentChangedEvent;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton
 */
public class EnvironmentPresenter extends AbstractPresenter<EnvironmentView> implements ChangeHandler {
    private String environmentName = null;

    private ToolkitEventBus eventBus;

    @Inject
    public EnvironmentPresenter(ToolkitEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void init() {
        // this should call reload()
        List<String> items = new ArrayList<>();
        items.add("default");
        view.set(items);
    }

    public void onChange(ChangeEvent unused) {
        environmentName = view.selection();
        view.updateSelection(environmentName);
        updateCookie();
        updateServer();

        eventBus.fireEvent(new EnvironmentChangedEvent(environmentName));

        new SetEnvironmentCommand().run(ClientUtils.INSTANCE.getCommandContext().setEnvironmentName(environmentName));
    }

    void reload() {
        new GetEnvironmentNamesCommand() {

            @Override
            public void onComplete(List<String> result) {
                view.set(result);
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());

        new SetEnvironmentCommand().run(ClientUtils.INSTANCE.getCommandContext().setEnvironmentName("default"));

    }

    private void updateCookie() {
        Cookies.removeCookie(CookieManager.ENVIRONMENTCOOKIENAME);
    }

    private void updateServer() {
        new SetEnvironmentCommand().run(ClientUtils.INSTANCE.getCommandContext());
    }

    public String getEnvironmentName() {
        return environmentName;
    }
}
