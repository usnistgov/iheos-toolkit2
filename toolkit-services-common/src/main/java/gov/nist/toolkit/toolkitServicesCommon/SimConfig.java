package gov.nist.toolkit.toolkitServicesCommon;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Access to the collection of properties that define a simulator. Some are common to all simulators and
 * some are custom to a particular simulator.
 */
public interface SimConfig extends SimId {
    /**
     * Set a property that takes a String value.
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @param value property value
     */
    void setProperty(String name, String value);

    /**
     * Set a property that takes a String value
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @param value property value
     */
    void setProperty(String name, List<String> value);

    /**
     * Set a property that takes a boolean value
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @param value property value
     */
    void setProperty(String name, boolean value);

    /**
     * Set a property that takes a PatientErrorMap value
     */
    void setPatientErrorMap(PatientErrorMap errorMap) throws IOException;

    PatientErrorMap getPatientErrorMap() throws IOException;

    /**
     * Is named property a boolean value?
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @return boolean
     */
    boolean isBoolean(String name);
    boolean isList(String name);

    boolean isString(String name);
    /**
     * Return named property as a String
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @return String value
     */
    String asString(String name);

    /**
     * Return named property as a list of Strings
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @return String list value
     */
    List<String> asList(String name);

    /**
     * Return named property as a String
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @return boolean value
     */
    boolean asBoolean(String name);

    /**
     * Describe Simulator Configuration.
     * @return Description string.
     */
    String describe();

    Collection<String> getPropertyNames();
}
