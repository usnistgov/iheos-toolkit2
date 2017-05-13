package gov.nist.toolkit.xdstools2.client.event.testSession;

import java.util.List;

/**
 *
 */
public interface TestSessionManager {
    List<String> getTestSessions();
    String getCurrentTestSession();
    void setTestSessions(List<String> testSessions);
    String fromCookie();
    boolean isLegalTestSession(String name);
    void setCurrentTestSession(String testSession);
}
