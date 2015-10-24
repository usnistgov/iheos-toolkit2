package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;

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
}
