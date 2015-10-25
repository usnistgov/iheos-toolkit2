package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Not for Public Use.
 */
@XmlRootElement
public class Prop {
    private boolean isBoolean;
    private String stringValue;
    private boolean booleanValue;

    public Prop(String value) {
        this.stringValue = value;
        this.isBoolean = false;
    }

    public Prop(boolean value) {
        this.booleanValue = value;
        this.isBoolean = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prop prop = (Prop) o;

        if (isBoolean != prop.isBoolean) return false;
        if (booleanValue != prop.booleanValue) return false;
        return !(stringValue != null ? !stringValue.equals(prop.stringValue) : prop.stringValue != null);

    }

    @Override
    public int hashCode() {
        int result = (isBoolean ? 1 : 0);
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        result = 31 * result + (booleanValue ? 1 : 0);
        return result;
    }

    public boolean isBooleanType() {
        return isBoolean;
    }

    public String asString() {
        return stringValue;
    }

    public boolean asBoolean() {
        return booleanValue;
    }

    @Override
    public String toString() {
        if (isBoolean) return  ((booleanValue) ? "true" : "false");
        return stringValue;
    }
}
