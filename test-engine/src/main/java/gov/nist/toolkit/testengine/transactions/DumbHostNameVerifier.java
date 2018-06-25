package gov.nist.toolkit.testengine.transactions;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DumbHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
