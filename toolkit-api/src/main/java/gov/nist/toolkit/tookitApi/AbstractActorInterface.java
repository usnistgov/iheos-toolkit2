package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.SimConfig;

/**
 * Created by bill on 10/31/15.
 */
public interface AbstractActorInterface {

    SimConfig getConfig();

     SimConfig update(SimConfig config) throws ToolkitServiceException;

     void delete() throws ToolkitServiceException ;

    /**
     * Set a property that takes a String value
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @param value property value
     */
    void setProperty(String name, String value);
    /**
     * Set a property that takes a boolean value
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @param value property value
     */
    void setProperty(String name, boolean value);
    /**
     * Is named property a boolean value?
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @return boolean
     */
    boolean isBoolean(String name);
    /**
     * Return named property as a String
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @return String value
     */
    String asString(String name);
    /**
     * Return named property as a String
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @return boolean value
     */
     boolean asBoolean(String name);
    /**
     * Describe Simulator Configuration.
     * @return Description string.
     */
     String describe();

     String getId();
}
