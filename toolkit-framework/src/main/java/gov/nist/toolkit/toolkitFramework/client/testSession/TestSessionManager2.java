package gov.nist.toolkit.toolkitFramework.client.testSession;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;
import gov.nist.toolkit.toolkitFramework.client.commands.AddMesaTestSessionCommand;
import gov.nist.toolkit.toolkitFramework.client.commands.DeleteMesaTestSessionCommand;
import gov.nist.toolkit.toolkitFramework.client.commands.GetTestSessionNamesCommand;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentState;
import gov.nist.toolkit.toolkitFramework.client.events.TestSessionsUpdatedEvent;
import gov.nist.toolkit.toolkitFramework.client.events.TestSessionsUpdatedEventHandler;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;
import gov.nist.toolkit.toolkitFramework.client.util.CookieManager;
import gov.nist.toolkit.toolkitFramework.client.util.CurrentCommandContext;

import javax.inject.Inject;
import java.util.List;


/**
 * When this finally replaced TestSessionManager it will loose the 2.
 * This is a singleton owned by Xdstools2 and should be reference through it. That
 * is where the current list is maintained so that new tabs can be initialized.
 */
public class TestSessionManager2 implements TestSessionManager {
//    private final EventBus eventBus = FrameworkGinInjector.INSTANCE.getEventBus();
    private List<String> testSessions;  // this is maintained to initialize new tabs with
    private String currentTestSession = "default";

    @Inject
    private EventBus eventBus;

    @Inject
    private EnvironmentState environmentState;

    public TestSessionManager2() {
        GWT.log("In TestSessionManager2");
        assert(eventBus != null);
        eventBus.addHandler(TestSessionsUpdatedEvent.TYPE, new TestSessionsUpdatedEventHandler() {
            @Override
            public void onTestSessionsUpdated(TestSessionsUpdatedEvent event) {
                testSessions = event.testSessionNames;
            }
        });
        eventBus.addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                switch (event.getChangeType()) {
                    case ADD:
                        add(event.getValue());
                        break;
                    case DELETE:
                        delete(event.getValue());
                        break;
                    case SELECT:
                        setCurrentTestSession(event.getValue());
                        toCookie(event.getValue());
                }
            }
        });
    }

    public List<String> getTestSessions() { return testSessions; }

    public String getCurrentTestSession() {
        return currentTestSession;
    }
    public void setCurrentTestSession(String testSession) {
        currentTestSession = testSession;
    }
    public boolean isTestSessionValid() { return !isEmpty(currentTestSession); }

    public String fromCookie() {
        String x = Cookies.getCookie(CookieManager.TESTSESSIONCOOKIENAME);
        if (x == null) return "";
        return x;
    }
    private void toCookie(String value) { Cookies.setCookie(CookieManager.TESTSESSIONCOOKIENAME, value);}
    private void deleteCookie() { Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);}

    // get sessionNames from server and broadcast to all tabs
    public void load() { load(fromCookie()); }
    public void load(final String initialSelection) {
        new GetTestSessionNamesCommand() {

            @Override
            public void onComplete(List<String> var1) {
                testSessions = var1;
                if (isLegalTestSession(initialSelection)) {
                    setCurrentTestSession(initialSelection);
                    toCookie(currentTestSession);
                } else {
                    if (isLegalTestSession(currentTestSession)) {
                        toCookie(currentTestSession);
                    } else {
                        setCurrentTestSession("");
                        deleteCookie();
                    }
                }
                eventBus.fireEvent(new TestSessionsUpdatedEvent(testSessions));
                eventBus.fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, currentTestSession));
            }
        }.run(CurrentCommandContext.GET());
    }

    // save new sessionName to server and broadcast updates to all tabs
    public void add(final String sessionName) {
        new AddMesaTestSessionCommand(){
            @Override
            public void onComplete(Boolean result) {
                load(sessionName);  // getRetrievedDocumentsModel full list and update all tabs
            }
        }.run(new CommandContext(environmentState.getEnvironmentName(),sessionName));
    }

    // delete new sessionName from server and broadcast updates to all tabs
    public void delete(String sessionName) {
        new DeleteMesaTestSessionCommand(){
            @Override
            public void onComplete(Boolean result) {
                currentTestSession=testSessions.get(0);
                load(currentTestSession);  // getRetrievedDocumentsModel full list and update all tabs
            }
        }.run(new CommandContext(environmentState.getEnvironmentName(),sessionName));
    }

    private boolean isEmpty(String x) { return x == null || x.trim().equals(""); }
    public boolean isLegalTestSession(String name) { return !isEmpty(name) && testSessions.contains(name); }

    public void setTestSessions(List<String> testSessions) {
        this.testSessions = testSessions;
    }
}
