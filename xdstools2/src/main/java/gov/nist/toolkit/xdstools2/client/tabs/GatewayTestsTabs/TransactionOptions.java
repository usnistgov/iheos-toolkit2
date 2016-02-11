package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

/**
 *
 */
public class TransactionOptions {
    boolean tls = false;
    boolean async = false;
    boolean saml = false;

    public boolean isTls() {
        return tls;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isSaml() {
        return saml;
    }

    public void setSaml(boolean saml) {
        this.saml = saml;
    }
}
