package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.*;

/**
 * only used locally
 */

public class QueryParametersResource {
    Map<String, List<String>> parms = new HashMap<>();

    public QueryParametersResource() {}

    public Set<String> getParameterNames() {
        return parms.keySet();
    }

    private List<String> getItem(String key) {
        if (key == null) return null;
        List<String> values = parms.get(key);
        if (values != null) return values;
        return new ArrayList<String>();
    }

    public void addParameter(String paramName, String value) {
        if (parms.containsKey(paramName)) {
            List<String> values = getItem(paramName);
            if (values == null) {
                values = new ArrayList<String>();
                parms.put(paramName, values);
            }
            values.add(value);
        } else {
            List<String> values = new ArrayList<>();
            values.add(value);
            parms.put(paramName, values);
        }
    }

    public void addParameter(String paramName, List<String> values) {
        List<String> values2 = getItem(paramName);
        if (values2 != null) {
            values2.addAll(values);
        } else {
            parms.put(paramName, values);
        }
    }

    public List<String> getValues(String paramName) {
        if (paramName == null) return new ArrayList<String>();
        List<String> values = parms.get(paramName);
        if (values == null) return new ArrayList<String>();
        return values;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("QueryParameters:\n");
        for (String key : parms.keySet()) {
            buf.append("   ").append(key).append(": ").append(parms.get(key)).append("\n");
        }

        return buf.toString();
    }

}
