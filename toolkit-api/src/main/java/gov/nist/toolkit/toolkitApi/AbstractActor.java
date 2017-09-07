package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.toolkitServicesCommon.RefList;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;
import gov.nist.toolkit.toolkitServicesCommon.resource.RefListResource;

import javax.ws.rs.core.Response;
import java.io.IOException;
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

   public void setConfig(SimConfig cnf) {
      config = cnf;
   }

   public EngineSpi getEngine() {
      return engine;
   }

   public void setEngine(EngineSpi eng) {
      engine = eng;
   }

   public void delete() throws ToolkitServiceException {
      engine.delete(config.getId(), config.getUser());
   }

    /**
     * Set a property that takes a String value
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @param value property value
     */
    public void setProperty(String name, String value) {
        config.setProperty(name, value);
//        if (SimulatorProperties.environment.equals((name))) {
//            config.setEnvironmentName(value);
//        }
    }
    /**
     * Set a property that takes a boolean value
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @param value property value
     */
    public void setProperty(String name, boolean value) {
        config.setProperty(name, value);
    }
    /**
     * Is named property a boolean value?
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @return boolean
     */
    public boolean isBoolean(String name) { return config.isBoolean(name);}

   public boolean isString(String name) {
      return config.isString(name);
   }

   public boolean isList(String name) {
      return config.isList(name);
   }

    /**
     * Return named property as a String
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @return String value
     */
    public String asString(String name) { return config.asString(name); }
    /**
     * Return named property as a String
     * @param name property name. See {@link SimulatorProperties} for property names.
     * @return boolean value
     */
    public boolean asBoolean(String name) { return config.asBoolean(name); }

   public List <String> asList(String name) {
      return config.asList(name);
   }

   /**
    * Describe Simulator Configuration.
    *
    * @return Description string.
    */
   public String describe() {
      return config.describe();
   }

   public String getId() {
      return config.getId();
   }

   public String getEnvironmentName() {
      return config.getEnvironmentName();
   }

   public String getActorType() {
      return config.getActorType();
   }

   public void setProperty(String name, List <String> value) {
      config.setProperty(name, value);
   }

   public String getFullId() {
      return config.getFullId();
   }

   public String getUser() {
      return config.getUser();
   }

   public Collection <String> getPropertyNames() {
      return config.getPropertyNames();
   }

   public RefList getEventIds(String simId, TransactionType transaction) throws ToolkitServiceException {
      Response response = engine.getTarget()
         .path(String.format("simulators/%s/events/%s", getConfig().getFullId(), transaction.getShortName())).request()
         .get();
      if (response.getStatus() != 200) throw new ToolkitServiceException(response);
      return response.readEntity(RefListResource.class);
   }

   public RefList getEvent(String simId, TransactionType transaction, String eventId) throws ToolkitServiceException {
      Response response =
         engine.getTarget()
            .path(
               String.format("simulators/%s/event/%s/%s", getConfig().getFullId(), transaction.getShortName(), eventId))
            .request().get();
      if (response.getStatus() != 200) throw new ToolkitServiceException(response);
      return response.readEntity(RefListResource.class);

   }

    @Override
    public void setPatientErrorMap(PatientErrorMap errorMap) throws IOException {
//        config.setPatientErrorMap(errorMap);
    }

    @Override
    public PatientErrorMap getPatientErrorMap() throws IOException {
        return null;
//        return config.getPatientErrorMap();
    }

    @Override
    public boolean isFhir() { return false; }
}
