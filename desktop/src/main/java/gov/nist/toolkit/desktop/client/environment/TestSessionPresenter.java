package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.CookieManager;
import gov.nist.toolkit.desktop.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.desktop.client.commands.*;
import gov.nist.toolkit.desktop.client.events.TestSessionChangedEvent;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;

import javax.inject.Inject;
import java.util.List;

/**
 *
 */
public class TestSessionPresenter extends AbstractPresenter<TestSessionView>  {
    private String testSessionName = "default";

    private ToolkitEventBus eventBus;

    private String defaultTestSession;

    ListBoxChangeHandler listBoxChangeHandler = new ListBoxChangeHandler();
    AddHandler addHandler = new AddHandler();
    DeleteHandler deleteHandler = new DeleteHandler();

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

                new GetDefaultTestSessionCommand() {
                    @Override
                    public void onComplete(String result) {
                        defaultTestSession = result;
                    }
                }.run(ClientUtils.INSTANCE.getCurrentCommandContext());
            }
        };

        t.schedule(5);
    }

    private void reload() {
        new GetTestSessionNamesCommand() {

            @Override
            public void onComplete(List<String> testSessionNames) {
                view.set(testSessionNames);
                view.updateSelection(testSessionName);
            }
        }.run(ClientUtils.INSTANCE.getCurrentCommandContext());

    }

    private void updateCookie() {
        Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);
    }

    public String getTestSessionName() {
        return testSessionName;
    }

    class ListBoxChangeHandler implements ChangeHandler {
        public void onChange(ChangeEvent unused) {
            testSessionName = view.selection();
            view.updateSelection(testSessionName);
            updateCookie();

            eventBus.fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT,  testSessionName));

            new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext());
        }
    }

    class AddHandler implements ClickHandler, KeyPressHandler {

        @Override
        public void onClick(ClickEvent unused) {
            add();
        }

        @Override
        public void onKeyPress(KeyPressEvent event_)
        {
            boolean enterPressed = KeyCodes.KEY_ENTER == event_
                    .getNativeEvent().getKeyCode();
            if (enterPressed) {
                add();
            }
        }

        private void add() {
            testSessionName = view.newText();
            updateCookie();

            eventBus.fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT,  testSessionName));

            new AddTestSessionCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext());
            reload();
            new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext());
        }

    }

    class DeleteHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            String value = view.selection();

            new DeleteTestSessionCommand().run(ClientUtils.INSTANCE.getCurrentCommandContext().setTestSessionName(value));
            reload();

            testSessionName = defaultTestSession;
            view.updateSelection(testSessionName);
        }
    }

}
