package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
class Mapping {
     String key;
     String value;

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
        this.key = key;
        this.value = value;
    }

    public Mapping() {}
}
