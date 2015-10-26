package gov.nist.toolkit.toolkitServicesCommon;

/**
 * Access to the collection of properties that define a simulator. Some are common to all simulators and
 * some are custom to a particular simulator.
 */
public interface SimConfig extends SimId {
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
}
