package gov.nist.toolkit.xdstools2.client.event.testSession;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.CookieManager;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestSessionNamesCommand;
import gov.nist.toolkit.xdstools2.client.event.TestSessionChangedEvent;

import java.util.List;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 * When this finally replaced TestSessionManager it will loose the 2.
 * This is a singleton owned by Xdstools2 and should be reference through it. That
 * is where the current list is maintained so that new tabs can be initialized.
 */
public class TestSessionManager2 {
    private List<String> testSessions;  // this is maintained to initialize new tabs with
    private String currentTestSession = null;

    public TestSessionManager2() {
        Xdstools2.getEventBus().addHandler(TestSessionsUpdatedEvent.TYPE, new TestSessionsUpdatedEventHandler() {
            @Override
            public void onTestSessionsUpdated(TestSessionsUpdatedEvent event) {
                testSessions = event.testSessionNames;
            }
        });
        Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                switch (event.changeType) {
                    case ADD:
                        add(event.value);
                        break;
                    case DELETE:
                        delete(event.value);
                        break;
                    case SELECT:
                        setCurrentTestSession(event.value);
                        toCookie(event.value);
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
                Xdstools2.getEventBus().fireEvent(new TestSessionsUpdatedEvent(testSessions));
                Xdstools2.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, currentTestSession));
            }
        }.run(Xdstools2.getHomeTab().getCommandContext());
    }

    // save new sessionName to server and broadcast updates to all tabs
    public void add(final String sessionName) {
        toolkitService.addMesaTestSession(sessionName, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot add test session - " + throwable.getMessage());
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                load(sessionName);  // getRetrievedDocumentsModel full list and update all tabs
            }
        });
    }

    // delete new sessionName from server and broadcast updates to all tabs
    public void delete(String sessionName) {
        toolkitService.delMesaTestSession(sessionName, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot delete test session - " + throwable.getMessage());
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                load();  // getRetrievedDocumentsModel full list and update all tabs
            }
        });
    }

    private boolean isEmpty(String x) { return x == null || x.trim().equals(""); }
    public boolean isLegalTestSession(String name) { return !isEmpty(name) && testSessions.contains(name); }

    public void setTestSessions(List<String> testSessions) {
        this.testSessions = testSessions;
    }
}
