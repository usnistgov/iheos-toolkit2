package gov.nist.toolkit.toolkitServicesCommon;

/**
 * Created by bill on 10/12/15.
 */
public interface SimConfig extends SimId {
    void setProperty(String name, String value);
    void setProperty(String name, boolean value);
    boolean isBoolean(String name);
    String asString(String name);
    boolean asBoolean(String name);
}
