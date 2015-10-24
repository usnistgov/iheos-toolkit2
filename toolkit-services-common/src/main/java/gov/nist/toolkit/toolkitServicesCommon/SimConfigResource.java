package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@XmlRootElement
public class SimConfigResource extends SimIdResource implements SimConfig {
     List<String> props = new ArrayList<>();

    public SimConfigResource() {}

    @Override
    public void setProperty(String name, String value) {
        rmProperty(name);
        props.add(new Mapping(name, value).asString());
    }

    private String getProperty(String name) {
        int index = findProperty(name);
        if (index == -1) return null;
        return new Mapping(props.get(index)).getValue();
    }

    private int findProperty(String name) {
        for (int i=0; i<props.size(); i++) {
            String keyvalue = props.get(i);
            if (new Mapping(keyvalue).getKey().equals(name)) return i;
        }
        return -1;
    }

    private void rmProperty(String name) {
        int index = findProperty(name);
        if (index != -1) props.remove(index);
    }

    @Override
    public void setProperty(String name, boolean value) {
        rmProperty(name);
        props.add(new Mapping(name, valueAsString(value)).asString());
    }

    public List<String> getProperties() { return props; }

    @Override
    public boolean isBoolean(String name) {
        String p = getProperty(name);
        return p != null && (p.equals("true") || p.equals("false"));
    }

    @Override
    public String asString(String name) {
        return getProperty(name);
    }

    @Override
    public boolean asBoolean(String name) {
        return valueAsBoolean(getProperty(name));
    }

    public Set<String> propertyNames() {
        Set<String> names = new HashSet<>();
        for (String s : props) {
            Mapping m = new Mapping(s);
            names.add(m.getKey());
        }
        return names;
    }

    public String describe() {
        StringBuilder buf = new StringBuilder();
        buf.append("SimConfigBean...\n");
        buf.append("...id = ").append(id).append("\n");
        buf.append("...user = ").append(user).append("\n");
        buf.append("...type = ").append(actorType).append("\n");
        for (String key : propertyNames()) {
            buf.append("...").append(key).append("=").append(getProperty(key)).append("\n");
        }
        return buf.toString();
    }

    private String valueAsString(boolean b) { return (b) ? "true" : "false"; }
    private boolean valueAsBoolean(String v) { return v.equals("true"); }
}
