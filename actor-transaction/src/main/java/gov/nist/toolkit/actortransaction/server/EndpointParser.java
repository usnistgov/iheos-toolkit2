package gov.nist.toolkit.actortransaction.server;

/**
 * Parse and validqte SOAP endpoints and HTTP urls
 */
public class EndpointParser  {
    private String endpoint;
    private String[] parts;
    private String error = null;

    public EndpointParser(String endpoint) {
        this.endpoint = endpoint;
        if (endpoint != null)
            this.parts = endpoint.split("\\/");
    }

    public boolean validate() {
        if (endpoint == null || endpoint.equals("") || parts == null || parts.length < 1) {
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

        for (int i=3; i<parts.length; i++) {
            buf.append("/").append(parts[i]);
        }

        return buf.toString();
    }

    public String getProtocol() {
        String[] cparts = parts[0].split(":");
        if (cparts.length < 1) return "";
        return cparts[0];
    }

    private boolean validateNoEmptyParts() {
        for (int i=2; i<parts.length; i++) {
            if (parts[i].equals("")) {
                error = "Endpoint contains empty path element";
                return false;
            }
        }
        return true;
    }

    private boolean validateNoParms() {
        if (parts[parts.length-1].contains("?")) {
            error = "Endpoint contains parameters";
            return false;
        }
        return true;
    }

    public String getError() {
        return error;
    }

    public EndpointParser updateHostAndPort(String host, String port) {
        parts[2] = host + ":" + port;
        return this;
    }

    public String getHost() {
        String[] hp = parts[2].split(":");
        return hp[0];
    }

    public String getPort() {
        String[] hp = parts[2].split(":");
        if (hp.length < 2) return "80";
        return hp[1];
    }

    public void setPort(String port) {
        String[] hp = parts[2].split(":");
        String r = hp[0] + ':' + port;
        parts[2] = r;
    }

    public String getEndpoint() {
        StringBuilder buf = new StringBuilder();

        buf.append(parts[0]);
        for (int i=1; i<parts.length; i++) {
            buf.append("/").append(parts[i]);
        }

        return buf.toString();
    }
}
