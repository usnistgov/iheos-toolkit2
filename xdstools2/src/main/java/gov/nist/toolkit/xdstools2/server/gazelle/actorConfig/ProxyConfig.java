package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

/**
 * Created by bmajur on 1/20/15.
 */
public class ProxyConfig {
    public String system;
    public String wsType;
    public String proxyPort;

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getWsType() {
        return wsType;
    }

    public void setWsType(String wsType) {
        this.wsType = wsType;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    static public String getHeader() { return "System, wsType, proxyPort"; }

    public String toCSV() {
        StringBuilder builder = new StringBuilder();
        builder.append(system);
        builder.append(", ").append(wsType);
        builder.append(", ").append(proxyPort);
        return builder.toString();
    }
}
