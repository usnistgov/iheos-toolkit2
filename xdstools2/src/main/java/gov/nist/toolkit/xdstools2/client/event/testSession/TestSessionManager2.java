package gov.nist.toolkit.xdstools2.client.event.testSession;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
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
        return currentTestSession;
    }
    public void setCurrentTestSession(String testSession) {
        currentTestSession = testSession;
    }
    public boolean isTestSessionValid() { return !isEmpty(currentTestSession); }

    String fromCookie() {
        String x = Cookies.getCookie(CookieManager.TESTSESSIONCOOKIENAME);
        if (x == null) return "";
        return x;
    }
    void toCookie(String value) { Cookies.setCookie(CookieManager.TESTSESSIONCOOKIENAME, value);}
    void deleteCookie() { Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);}

    // get sessionNames from server and broadcast to all tabs
    public void load() { load(fromCookie()); }
    public void load(final String initialSelection) {
//        Xdstools2.DEBUG("initialSelection is " + initialSelection + ".  currentTestSession is " + currentTestSession);

        toolkitService.getMesaTestSessionNames(new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot load test session names - " + throwable.getMessage());
            }

            @Override
            public void onSuccess(List<String> newTestSessions) {
//                Xdstools2.DEBUG("newTestSessions = " + newTestSessions);
                testSessions = newTestSessions;

                if (isLegalTestSession(initialSelection)) {
//                    Xdstools2.DEBUG("initialSeletion is legal");
                    currentTestSession = initialSelection;
                    toCookie(currentTestSession);
//                    Xdstools2.DEBUG("set cookie to " + currentTestSession);
                } else {
                    if (isLegalTestSession(currentTestSession)) {
//                        Xdstools2.DEBUG("currentTestSelection, " + currentTestSession + ", is legal");
                        toCookie(currentTestSession);
//                        Xdstools2.DEBUG("set cookie to " + currentTestSession);
                    } else {
                        currentTestSession = "";
//                        Xdstools2.DEBUG("delete cookie");
                        deleteCookie();
                    }
                }
//                Xdstools2.DEBUG("currentTestSelection is " + currentTestSession);
//                Xdstools2.DEBUG("cookie is " + fromCookie());
                Xdstools2.getEventBus().fireEvent(new TestSessionsUpdatedEvent(testSessions));
                Xdstools2.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, currentTestSession));

                try {
                    toolkitService.getAllSimConfigs(currentTestSession, new AsyncCallback<List<SimulatorConfig>>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            new PopupMessage("Cannot load sim configs - " + throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(List<SimulatorConfig> simulatorConfigs) {
                            try {
                                toolkitService.getTransactionOfferings(new AsyncCallback<TransactionOfferings>() {
                                    @Override
                                    public void onFailure(Throwable throwable) {

                                    }

                                    @Override
                                    public void onSuccess(TransactionOfferings transactionOfferings) {

                                    }
                                });
                            } catch (Exception e) {
                                new PopupMessage("getTransactionOfferings failed - " + e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    new PopupMessage("Cannot load sim configs - " + e.getMessage());

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

    boolean isEmpty(String x) { return x == null || x.trim().equals(""); }
    boolean isLegalTestSession(String name) { return !isEmpty(name) && testSessions.contains(name); }
}
