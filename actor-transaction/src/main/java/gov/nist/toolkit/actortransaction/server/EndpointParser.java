package gov.nist.toolkit.actortransaction.server;

import org.apache.http.HttpHost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parse and validqte SOAP endpoints and HTTP urls
 */
public class EndpointParser  {
    private String endpoint;
    private List<String> parts;
    private String error = null;

    public EndpointParser(String endpoint) {
        this.endpoint = endpoint;
        if (endpoint != null) {
            List<String> theList = Arrays.asList(endpoint.split("\\/"));
            parts = new ArrayList<String>();
            for (String x : theList)
                parts.add(x);
        }
    }

    public String toString() { return endpoint; }

    public boolean validate() {
        if (endpoint == null || endpoint.equals("") || parts == null || parts.size() < 1) {
            error = "null or empty endpoint";
            return false;
        }
        String protocol = getProtocol();
        if (!protocol.startsWith("http")) {
            error = String.format("Do not understand getProtocol %s, only http and https accepted", protocol);
            return false;
        }
        if (!validateNoEmptyParts()) return false;
        if (!validateNoParms()) return false;
        return true;
    }

    public String getService() {
        StringBuilder buf = new StringBuilder();

        for (int i=3; i<parts.size(); i++) {
            buf.append("/").append(parts.get(i));
        }

        return buf.toString();
    }

    public String getContext() {
        if (parts.size() <= 3)
            return "";

        // if running as tomcat root there is no context
        if (parts.get(3).equals("sim") || parts.get(3).equals("fsim"))
            return "";

        return parts.get(3);

    }

    public void setContext(String context) {
        if (parts.size() > 3) {
            if (parts.get(3).equals("sim") || parts.get(3).equals("fsim")) {
                parts.add(3, context);
            } else {
                parts.set(3, context);
            }
        }
    }

    public String getProtocol() {
        String[] cparts = parts.get(0).split(":");
        if (cparts.length < 1) return "";
        return cparts[0];
    }

    public HttpHost getHttpHost() {
        return HttpHost.create(
                getProtocol() +
                        "://" +
                        getHost() +
                        ":" +
                        getPort()
        );
    }

    private boolean validateNoEmptyParts() {
        for (int i=2; i<parts.size(); i++) {
            if (parts.get(i).equals("")) {
                error = "Endpoint contains empty path element";
                return false;
            }
        }
        return true;
    }

    private boolean validateNoParms() {
        if (parts.get(parts.size()-1).contains("?")) {
            error = "Endpoint contains parameters";
            return false;
        }
        return true;
    }

    public String getError() {
        return error;
    }

    public EndpointParser updateHostAndPort(String host, String port) {
        parts.set(2, host + ":" + port);
        return this;
    }

    public String getHost() {
        String[] hp = parts.get(2).split(":");
        return hp[0];
    }

    public String getPort() {
        String[] hp = parts.get(2).split(":");
        if (hp.length < 2) return "80";
        return hp[1];
    }

    public void setPort(String port) {
        String[] hp = parts.get(2).split(":");
        String r = hp[0] + ':' + port;
        parts.set(2, r);
    }

    /**
     * 0 - http
     * 1 - ""
     * 2 - host:port
     * 3 - service
     * if service is "" do not include double //
     * @return
     */
    public String getEndpoint() {
        StringBuilder buf = new StringBuilder();

        buf.append(parts.get(0));
        for (int i=1; i<parts.size(); i++) {
            if (i == 3 && parts.get(i).equals("") )
                continue;
            buf.append("/").append(parts.get(i));
        }

        return buf.toString();
    }

}
