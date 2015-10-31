package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.actortransaction.SimulatorActorType;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;
import gov.nist.toolkit.toolkitServicesCommon.SimId;

/**
 * Build/modify a collection of different Actors.
 */
public class SimulatorBuilder {
    String host;
    String port;
    EngineSpi engine;


    /**
     * This will initialize the API to contact the test engine at
     * http://hostname:port/xdstools2
     * @param host where engine is running
     * @param port where engine is running
     */
    public SimulatorBuilder(String host, String port) {
        this.host = host;
        this.port = port;
        engine = new EngineSpi(host, port);
    }

    private SimConfig create(String id, String user, SimulatorActorType actorType, String environmentName) throws ToolkitServiceException {
        return engine.create(id, user, actorType, environmentName);
    }

    /**
     * Create new simulator with default parameters. There is currently no way to create a simulator with
     * custom parameters.  Update the SimConfig
     * returned and then issue an update(SimConfig) to update the simulator configuration.
     * The parameters defining a simulator will change over time.  To create a custom
     * configuration use this call to create the simulator with the default parameters.
     * @param id Simulator ID
     * @param user User creating Simulator.  Same as TestSession in Toolkit UI. The simulator ID must be unique for this user.
     * @param environmentName Environment defines Affinity Domain coding schemes and TLS certificate for use with client.
     * @return Simulator configuration.
     * @throws ToolkitServiceException if anything goes wrong.
     */
    public DocumentSource createDocumentSource(String id, String user, String environmentName) throws ToolkitServiceException {
        XdrDocumentSource src = new XdrDocumentSource();
        src.engine = engine;
        src.config =  engine.create(id, user, SimulatorActorType.DOCUMENT_SOURCE, environmentName);
        return src;
    }

    public DocumentRecipient createDocumentRecipient(String id, String user, String environmentName) throws ToolkitServiceException {
        XdrDocumentRecipient act = new XdrDocumentRecipient();
        act.engine = engine;
        act.config = engine.create(id, user, SimulatorActorType.DOCUMENT_RECIPIENT, environmentName);
        return act;
    }

    /**
     * Update the configuration of an existing Simulator. Any properties that are passed in SimConfig that are
     * not recognized will be silently ignored. Parameters passed with wrong type (String vs. boolean) will cause
     * ToolkitServiceException.
     *
     * Expected usage is to retrieve the configuration using the get() method,
     * update the parameters, and then submit the update using this call.
     * @param config new configuration
     * @return updated SimConfig if updates made or null if no changes accepted.
     * @throws ToolkitServiceException if anything goes wrong
     */
    public SimConfig update(SimConfig config) throws ToolkitServiceException {
        return engine.update(config);
    }

    /**
     * Delete an existing simulator. There is another call available using the SimId parameter type.  This
     * parameter type contains the raw ID and USER that are used here.  The two calls function identically.
     * If the simulator does not exist no error is returned.
     * @param id of simulator
     * @param user of simulator
     * @throws ToolkitServiceException if anything goes wrong.
     */
    public void delete(String id, String user) throws ToolkitServiceException {
        engine.delete(id, user);
    }

    /**
     * Delete an existing simulator. There is another call available using separate raw ID and USER parameters.
     * USER and ID are components of the more formal SimId type.  The two calls function identically.
     * If the simulator does not exist no error is returned.
     * @param simId Simulator ID
     * @throws ToolkitServiceException if anything goes wrong
     */
    public void delete(SimId simId) throws ToolkitServiceException {
        engine.delete(simId);
    }

    /**
     * Get full configuration of existing Simulator as defined by its Simulator ID
     * @param simId simulator ID
     * @return simulator configuration
     * @throws ToolkitServiceException if anything goes wrong
     */
    public SimConfig get(SimId simId) throws ToolkitServiceException {
        return engine.get(simId);
    }

}
