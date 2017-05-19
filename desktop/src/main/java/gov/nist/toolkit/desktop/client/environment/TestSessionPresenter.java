package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
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
    }

    @Override
    public void init() {
        // this should call reload()
        List<String> items = new ArrayList<>();
        items.add("default");
        view.set(items);
    }

    public void onChange(ChangeEvent unused) {
        testSessionName = view.selection();
        view.updateSelection(testSessionName);
        updateCookie();
        updateServer();

        eventBus.fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT,  testSessionName));

        CommandContext cc = ClientUtils.INSTANCE.getCommandContext();
        cc.setTestSessionName(testSessionName);
        new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCommandContext());
    }

    public void onAdd() {

    }

    public void onDelete() {

    }

    void reload() {
        new GetTestSessionNamesCommand() {

            @Override
            public void onComplete(List<String> result) {
                view.set(result);
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());

        new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCommandContext().setEnvironmentName("default"));

    }

    private void updateCookie() {
        Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);
    }

    private void updateServer() {
        new SetTestSessionCommand().run(ClientUtils.INSTANCE.getCommandContext());
    }

    public String getTestSessionName() {
        return testSessionName;
    }
}
