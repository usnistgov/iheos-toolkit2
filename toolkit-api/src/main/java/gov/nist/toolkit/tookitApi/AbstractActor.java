package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.toolkitServicesCommon.RefList;
import gov.nist.toolkit.toolkitServicesCommon.RefListResource;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;

import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

/**
 *
 */
abstract class AbstractActor implements AbstractActorInterface {
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

    public boolean isString(String name) { return config.isString(name); }

    public boolean isList(String name) { return config.isList(name); }

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

    public List<String> asList(String name) { return config.asList(name); }
    /**
     * Describe Simulator Configuration.
     * @return Description string.
     */
    public String describe() { return config.describe(); }

    public String getId() { return config.getId(); }

    public String getEnvironmentName() { return config.getEnvironmentName(); }

    public String getActorType() { return config.getActorType(); }

    public void setProperty(String name, List<String> value) { config.setProperty(name, value); }

    public String getFullId() { return config.getFullId(); }

    public String getUser() { return config.getUser(); }

    public Collection<String> getPropertyNames() { return config.getPropertyNames(); }

    public RefList getEventIds(String simId, TransactionType transaction) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/events/%s", getConfig().getFullId(), transaction.getShortName()))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(RefListResource.class);
    }

    public RefList getEvent(String simId, TransactionType transaction, String eventId) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/event/%s/%s", getConfig().getFullId(), transaction.getShortName(), eventId))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(RefListResource.class);

    }

}
