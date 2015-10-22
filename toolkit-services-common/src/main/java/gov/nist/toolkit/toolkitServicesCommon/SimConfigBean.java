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
public class SimConfigBean extends SimIdBean implements SimConfig {
     List<Mapping> props = new ArrayList<Mapping>();

    public SimConfigBean() {}

    @Override
    public void setProperty(String name, String value) {
        rmProperty(name);
        props.add(new Mapping(name, value));
    }

    private String getProperty(String name) {
        int index = findProperty(name);
        if (index == -1) return null;
        return props.get(index).getValue();
    }

    private int findProperty(String name) {
        for (int i=0; i<props.size(); i++) if (props.get(i).getKey().equals(name)) return i;
        return -1;
    }

    private void rmProperty(String name) {
        int index = findProperty(name);
        if (index != -1) props.remove(index);
    }

    @Override
    public void setProperty(String name, boolean value) {
        rmProperty(name);
        props.add(new Mapping(name, valueAsString(value)));
    }

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

    public Set<String> getPropertyNames() {
        Set<String> names = new HashSet<>();
        for (Mapping m : props) {
            names.add(m.getKey());
        }
        return names;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("SimConfigBean...\n");
        buf.append("...id = ").append(id).append("\n");
        buf.append("...user = ").append(user).append("\n");
        buf.append("...type = ").append(actorType).append("\n");
        for (String key : getPropertyNames()) {
            buf.append("...").append(key).append("=").append(getProperty(key)).append("\n");
        }
        return buf.toString();
    }

    private String valueAsString(boolean b) { return (b) ? "true" : "false"; }
    private boolean valueAsBoolean(String v) { return v.equals("true"); }
}
