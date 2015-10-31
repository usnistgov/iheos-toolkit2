package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.SimConfig;

/**
 *
 */
abstract public class AbstractActor {
    EngineSpi engine;
    SimConfig config;

    public SimConfig getConfig() {
        return config;
    }

    public SimConfig update(SimConfig config) throws ToolkitServiceException {
        config = engine.update(config);
        return config;
    }

    public void delete() throws ToolkitServiceException {
        engine.delete(config.getId(), config.getUser());
    }

    /**
     * Set a property that takes a String value
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @param value property value
     */
    public void setProperty(String name, String value) { config.setProperty(name, value);}
    /**
     * Set a property that takes a boolean value
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @param value property value
     */
    public void setProperty(String name, boolean value) { config.setProperty(name, value);}
    /**
     * Is named property a boolean value?
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @return boolean
     */
    public boolean isBoolean(String name) { return config.isBoolean(name);}
    /**
     * Return named property as a String
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @return String value
     */
    public String asString(String name) { return config.asString(name); }
    /**
     * Return named property as a String
     * @param name property name. See {@link gov.nist.toolkit.actorfactory.SimulatorProperties} for property names.
     * @return boolean value
     */
    public boolean asBoolean(String name) { return config.asBoolean(name); }
    /**
     * Describe Simulator Configuration.
     * @return Description string.
     */
    public String describe() { return config.describe(); }

    public String getId() { return config.getId(); }
}
