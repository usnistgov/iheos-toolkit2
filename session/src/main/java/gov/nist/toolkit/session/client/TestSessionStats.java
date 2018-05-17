package gov.nist.toolkit.session.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.installation.shared.ExpirationPolicy;
import gov.nist.toolkit.installation.shared.TestSession;

import java.io.Serializable;

public class TestSessionStats implements Serializable, IsSerializable {
    private TestSession testSession;
    private boolean expired;
    private String expires;
    private String lastUpdated;

    private ExpirationPolicy expirationPolicy;

    public TestSessionStats() {
    }

    public TestSession getTestSession() {
        return testSession;
    }

    public void setTestSession(TestSession testSession) {
        this.testSession = testSession;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public ExpirationPolicy getExpirationPolicy() {
        return expirationPolicy;
    }

    public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
        this.expirationPolicy = expirationPolicy;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
