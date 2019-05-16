package gov.nist.toolkit.configDatatypes.server;

public class FilterProxyProperties {
    static String prefix = "RelayTo_";

    static public String getRelayEndpointName(String endpointName) {
        return prefix + endpointName;
    }

    static public String getEndpointName(String relayEndpointName) {
        return relayEndpointName.replaceFirst(prefix, "");
    }

}
