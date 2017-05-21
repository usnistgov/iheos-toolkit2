package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.CookieManager;
import gov.nist.toolkit.desktop.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.desktop.client.commands.GetTestSessionNamesCommand;
import gov.nist.toolkit.desktop.client.commands.SetTestSessionCommand;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.events.TestSessionChangedEvent;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestSessionPresenter extends AbstractPresenter<TestSessionView> implements ChangeHandler {
    private String testSessionName = null;

    private ToolkitEventBus eventBus;

    @Inject
    public TestSessionPresenter(ToolkitEventBus eventBus) {
        this.eventBus = eventBus;
        assert(ClientUtils.INSTANCE != null);
    }

    @Override
    public void init() {

        // This timer is necessary because inside reload() we call ClientUtils.INSTANCE.getCurrentCommandContext()
        // which depends on this class be fully initialized.  The delay lets the initialization
        // complete
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
        testSessionName = view.selection();
        view.updateSelection(testSessionName);
        updateCookie();
        updateServer();

        eventBus.fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT,  testSessionName));

        new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext().setTestSessionName(testSessionName));
    }

    public void onAdd() {

    }

    public void onDelete() {

    }

    private void reload() {
        new GetTestSessionNamesCommand() {

            @Override
            public void onComplete(List<String> testSessionNames) {
                view.set(testSessionNames);
            }
        }.run(ClientUtils.INSTANCE.getCurrentCommandContext());

//        new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCommandContext().setEnvironmentName("default"));

    }

    private void updateCookie() {
        Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);
    }

    private void updateServer() {
        new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext());
    }

    public String getTestSessionName() {
        return testSessionName;
    }
}
