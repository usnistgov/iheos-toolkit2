package gov.nist.toolkit.xdstools2.client.event.testSession;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.*;

import java.util.List;

/**
 * When this finally replaced TestSessionManager it will loose the 2.
 * This is a singleton owned by Xdstools2 and should be reference through it. That
 * is where the current list is maintained so that new tabs can be initialized.
 */
public class TestSessionManager2 {
    final public ToolkitServiceAsync toolkitService = GWT
            .create(ToolkitService.class);

    List<String> testSessions;  // this is maintained to initialize new tabs with
    String currentTestSession;

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
                        currentTestSession = event.value;
                        toCookie(event.value);
                }
            }
        });
    }

    public List<String> getTestSessions() { return testSessions; }

    public String getCurrentTestSession() {
        Xdstools2.DEBUG("getCurrentTestSession() - " + currentTestSession);
        return currentTestSession;
    }
    public void setCurrentTestSession(String testSession) {
        Xdstools2.DEBUG("setTestSession(" + testSession + ")");
        currentTestSession = testSession;
    }

    String fromCookie() { return Cookies.getCookie(CookieManager.TESTSESSIONCOOKIENAME); }
    void toCookie(String value) { Cookies.setCookie(CookieManager.TESTSESSIONCOOKIENAME, value);}
    void deleteCookie() { Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);}

    // get sessionNames from server and broadcast to all tabs
    public void load() { load(fromCookie()); }
    public void load(final String newSelection) {
        Xdstools2.DEBUG("load(" + newSelection + ")");
        toolkitService.getMesaTestSessionNames(new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot load test session names - " + throwable.getMessage());
            }

            @Override
            public void onSuccess(List<String> testSessionNames) {
                Xdstools2.getEventBus().fireEvent(new TestSessionsUpdatedEvent(testSessionNames));
                if (!newSelection.isEmpty()) {
                    currentTestSession = newSelection;
                    if (testSessionNames.contains(newSelection)) {
                        toCookie(newSelection);
                        Xdstools2.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, newSelection));
                    } else {
                        deleteCookie();
                    }
                }
            }
        });
    }

    // save new sessionName to server and broadcast updates to all tabs
    public void add(final String sessionName) {
        toolkitService.addMesaTestSession(sessionName, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot load test session names - " + throwable.getMessage());
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                load(sessionName);  // get full list and update all tabs
            }
        });
    }

    // delete new sessionName from server and broadcast updates to all tabs
    public void delete(String sessionName) {
        toolkitService.delMesaTestSession(sessionName, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot load test session names - " + throwable.getMessage());
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                load();  // get full list and update all tabs
            }
        });
    }
}
