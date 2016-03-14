package gov.nist.toolkit.simulators.sim.cons;

import java.util.*;

/**
 *
 */
public class QueryParameters {

    Map<String, List<String>> parms = new HashMap<>();

    public QueryParameters() {}

    public Set<String> getParameterNames() { return parms.keySet(); }

    public void addParameter(String paramName, String value) {
        List<String> values = parms.get(paramName);
        if (values == null) {
            values = new ArrayList<String>();
            parms.put(paramName, values);
        }
        values.add(value);
    }

    public void addParameter(String paramName, List<String> values) {
        if (values == null) return;
        List<String> evalues = parms.get(paramName);
        if (evalues == null) {
            evalues = new ArrayList<String>();
            parms.put(paramName, evalues);
        }
        for (String value : values)
            evalues.add(value);
    }

    public List<String> getValues(String paramName) {
        return parms.get(paramName);
    }

}
