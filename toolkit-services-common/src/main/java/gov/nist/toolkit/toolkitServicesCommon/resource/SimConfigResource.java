package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Not for Public Use.
 */
@XmlRootElement
public class SimConfigResource extends SimIdResource implements SimConfig {
     List<String> props = new ArrayList<String>();

    public SimConfigResource() {}

    @Override
    public void setPatientErrorMap(PatientErrorMap errorMap) throws IOException {
//        setProperty("PatientErrorMap", PatientErrorMapIO.marshal(errorMap));
    }

    @Override
    public PatientErrorMap getPatientErrorMap() throws IOException {
        return null;
//        return PatientErrorMapIO.unmarshal(getProperty("PatientErrorMap"));
    }

    @Override
    public void setProperty(String name, String value) {
        rmProperty(name);
        props.add(new Mapping(name, value).asString());
    }

    @Override
    public void setProperty(String name, List<String> values) {
        rmProperty(name);
        props.add(new Mapping(name, values).asString());
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
    public boolean isString(String name) {
        if (isBoolean(name)) return false;
        String p = getProperty(name);
        if (p == null) return false;
        if (p.startsWith("[")) return false;
        return true;
    }

    @Override
    public boolean isList(String name) {
        if (isBoolean(name)) return false;
        String p = getProperty(name);
        if (p == null) return false;
        if (p.startsWith("[")) return true;
        return false;
    }

    @Override
    public String asString(String name) {
        return getProperty(name);
    }

    @Override
    public List<String> asList(String name) {
        String p = getProperty(name);
        return Mapping.asList(p);
    }

    @Override
    public boolean asBoolean(String name) {
        return valueAsBoolean(getProperty(name));
    }

    public Set<String> getPropertyNames() {
        Set<String> names = new HashSet<String>();
        for (String s : props) {
            Mapping m = new Mapping(s);
            names.add(m.getKey());
        }
        return names;
    }

    /**
     * Verbose description of Simulator Configuration.
     * @return description
     */
    public String describe() {
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
