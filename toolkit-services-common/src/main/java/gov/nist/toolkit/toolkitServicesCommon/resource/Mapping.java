package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@XmlRootElement
class Mapping {
     String key;
     String value;
    static final String separator = "=";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Mapping(String key, String value) {
        this.key = key.trim();
        this.value = value.trim();
    }

    public Mapping(String[] pair) {
        this(pair[0], pair[1]);
    }

    public String asString() { return key + separator + value; }

    public Mapping(String pair) {
       this(pad(pair).split(separator));
    }

    private static String pad(String in) { return in + " "; }

    public Mapping() {}

    // handling of value when it is a list strings

    public Mapping(String key, List<String> values) {
        this.key = key;
        this.value = encodeList(values);
    }

    static public List<String> asList(String value) {
        return decodeList(value);
    }

    private String encodeList(List<String> lst) {
        StringBuilder buf = new StringBuilder();
        for (String x : lst) {
            if (buf.length() > 0) buf.append(',');
            buf.append(x);
        }
        return String.format("[%s]", buf);
    }

    static private List<String> decodeList(String value) {
        List<String> lst = new ArrayList<String>();
        if (value == null) return lst;
        value = value.trim();
        if (value.charAt(0) != '[' || value.charAt(value.length()-1) != ']')
            return lst;
        value = value.substring(1, value.length()-1);
        String values[] = value.split(",");
        if (values.length == 0) return lst;
        for (int i=0; i<values.length; i++) {
            String x = values[i];
            if (x.length() > 0)
                lst.add(values[i]);
        }
        return lst;
    }
}
