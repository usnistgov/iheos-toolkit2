package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlType;

/**
 * Not for Public Use.
 */
@XmlType
public class KeyValue {
    String key;
    String value;

    public KeyValue() {
    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

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
}
