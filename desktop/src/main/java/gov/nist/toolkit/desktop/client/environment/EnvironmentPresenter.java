package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.CookieManager;
import gov.nist.toolkit.desktop.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.desktop.client.commands.GetEnvironmentNamesCommand;
import gov.nist.toolkit.desktop.client.commands.SetEnvironmentCommand;
import gov.nist.toolkit.desktop.client.events.EnvironmentChangedEvent;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;

import javax.inject.Inject;
import java.util.List;

/**
 * Singleton
 */
public class EnvironmentPresenter extends AbstractPresenter<EnvironmentView> implements ChangeHandler {
    private String environmentName = "default";

    private ToolkitEventBus eventBus;

    @Inject
    public EnvironmentPresenter(ToolkitEventBus eventBus) {
        this.eventBus = eventBus;
        assert(ClientUtils.INSTANCE != null);
    }

    @Override
    public void init() {

        // This timer is necessary because inside reload() we call ClientUtils.INSTANCE.getCurrentCommandContext()
        // which depends on this class be fully initialized.  The delay lets the initialization
        // complete.
        // The amount of delay is not important, The timer won't be looked at until initialization completes
        Timer t = new Timer() {

            @Override
            public void run() {
                reload();
            }
        };

        t.schedule(5);

    }

    public void onChange(ChangeEvent unused) {
        environmentName = view.selection();
        view.updateSelection(environmentName);
        updateCookie();
        updateServer();

        eventBus.fireEvent(new EnvironmentChangedEvent(environmentName));
    }

    private void reload() {
        new GetEnvironmentNamesCommand() {

            @Override
            public void onComplete(List<String> environmentNames) {
                view.set(environmentNames);
            }
        }.run(ClientUtils.INSTANCE.getCurrentCommandContext());

//        new SetEnvironmentCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext().setEnvironmentName(/* value not used */ "default"));

    }

    private void updateCookie() {
        Cookies.removeCookie(CookieManager.ENVIRONMENTCOOKIENAME);
    }

    // Updates environment selection and testsession selection
    // this is repeated in TestSessionPresenter
    private void updateServer() {
        new SetEnvironmentCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext());
    }

    public String getEnvironmentName() {
        return environmentName;
    }
}
