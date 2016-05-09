package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.toolkitServicesCommon.RefList;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;
import gov.nist.toolkit.toolkitServicesCommon.resource.SimConfigResource;

/**
 *
 */
public interface AbstractActorInterface extends SimConfig {

    SimConfig getConfig();

     SimConfigResource update(SimConfigResource config) throws ToolkitServiceException;

    /**
     * Delete the actor.
     * @throws ToolkitServiceException if something goes wrong.
     */
     void delete() throws ToolkitServiceException ;

    /**
     * Set a property that takes a String value
     * @param name property name. See {@link gov.nist.toolkit.configDatatypes.SimulatorProperties} for property names.
     * @param value property value
     */
    void setProperty(String name, String value);
    /**
     * Set a property that takes a boolean value
     * @param name property name. See {@link gov.nist.toolkit.configDatatypes.SimulatorProperties} for property names.
     * @param value property value
     */
    void setProperty(String name, boolean value);
    /**
     * Is named property a boolean value?
     * @param name property name. See {@link gov.nist.toolkit.configDatatypes.SimulatorProperties} for property names.
     * @return boolean
     */
    boolean isBoolean(String name);
    /**
     * Return named property as a String
     * @param name property name. See {@link gov.nist.toolkit.configDatatypes.SimulatorProperties} for property names.
     * @return String value
     */
    String asString(String name);
    /**
     * Return named property as a String
     * @param name property name. See {@link gov.nist.toolkit.configDatatypes.SimulatorProperties} for property names.
     * @return boolean value
     */
     boolean asBoolean(String name);
    /**
     * Describe Simulator Configuration.
     * @return Description string.
     */
     String describe();

     String getId();

    RefList getEventIds(String simId, TransactionType transaction) throws ToolkitServiceException;

    RefList getEvent(String simId, TransactionType transaction, String eventId) throws ToolkitServiceException;

}
