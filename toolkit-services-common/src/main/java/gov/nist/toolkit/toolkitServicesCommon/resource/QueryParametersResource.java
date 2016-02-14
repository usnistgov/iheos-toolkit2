package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@XmlRootElement
public class QueryParametersResource {
    String key1 = null;
    List<String> values1 = new ArrayList<String>();
//    String valuea1;

    public QueryParametersResource() {}

    public Set<String> getParameterNames() {
        Set<String> names = new HashSet<String>();
        if (key1 != null) names.add(key1);
        return names;
    }

    private List<String> getItem(String key) {
        if (key == null) return null;
        if (key.equals(key1)) return values1;
        return null;
    }

    public void addParameter(String paramName, String value) {
        List<String> values = getItem(paramName);
        if (values != null) { values.add(value); return; }
        if (key1 == null) {
            key1 = paramName;
            values1.add(value);
//            valuea1 = value;
            return;
        }
        throw new RuntimeException("QueryParameters: out of space");
    }

    // this doesn't merge lists as a set would - problem?
    public void addParameter(String paramName, List<String> values) {
        List<String> values2 = getItem(paramName);
        if (values2 != null) { values2.addAll(values); return; }
        if (key1 == null) { key1 = paramName; values1.addAll(values); return; }
        throw new RuntimeException("QueryParameters: out of space");
    }

    public List<String> getValues(String paramName) {
        if (paramName == null) return new ArrayList<String>();
        if (paramName.equals(key1)) return values1;
        return new ArrayList<String>();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("QueryParameters:\n");
        buf.append("   ").append(key1).append(": ").append(values1).append("\n");

        return buf.toString();
    }

}
