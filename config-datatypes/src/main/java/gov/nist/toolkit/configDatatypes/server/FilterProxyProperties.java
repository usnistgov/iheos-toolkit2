package gov.nist.toolkit.configDatatypes.server;

import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.util.HashMap;
import java.util.Map;

public class FilterProxyProperties {
    static Map<String, String> endpointToRelayEndpoint = new HashMap<>();
    static Map<String, String> relayEndpointToEndpoint = new HashMap<>();

    public FilterProxyProperties() {
        if (endpointToRelayEndpoint.isEmpty()) {
            init();
        }
    }

    public String getRelayEndpointName(String endpointName) {
        return endpointToRelayEndpoint.get(endpointName);
    }

    public String getEndpointName(String relayEndpointName) {
        return relayEndpointToEndpoint.get(relayEndpointName);
    }

    private void init() {
        for (TransactionType tt : TransactionType.values()) {
            String endpointPropName = tt.getEndpointSimPropertyName();
            String relayEndpointPropName = "RelayTo_" + endpointPropName;

            endpointToRelayEndpoint.put(endpointPropName, relayEndpointPropName);
            relayEndpointToEndpoint.put(relayEndpointPropName, endpointPropName);
        }
    }
}
