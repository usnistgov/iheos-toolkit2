package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.actortransaction.SimulatorActorType;
import gov.nist.toolkit.toolkitServicesCommon.SimConfig;
import gov.nist.toolkit.toolkitServicesCommon.SimId;

/**
 * Build/modify a collection of different Actor simulators running in a remote copy of toolkit.
 */
public class SimulatorBuilder {
    String urlRoot;
    EngineSpi engine;


    /**
     * This will initialize the API to contact the test engine at
     * http://hostname:port/xdstools2
     * @param urlRoot where engine is running - typical value would be http://localhost:8080/xdstools2
     */
    public SimulatorBuilder(String urlRoot) {
        this.urlRoot = urlRoot;
        engine = new EngineSpi(urlRoot);
    }

    private SimConfig create(BasicSimParameters p) throws ToolkitServiceException {
        return create(p.getId(), p.getUser(), p.getActorType(), p.getEnvironmentName());
    }

    private SimConfig create(String id, String user, SimulatorActorType actorType, String environmentName) throws ToolkitServiceException {
        return engine.create(id, user, actorType, environmentName);
    }

    /**
     * Create new Document Source simulator with default configuration.
     * To create a simulator with
     * custom configuration:
     * <ol>
     * <li>Create simulator with default configuration</li>
     * <li>Update the local copy of the configuration</li>
     * <li>Send the update via the update method</li>
     * </ol>
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

    /**
     * Create new Document Recipient simulator with default configuration.
     * To create a simulator with
     * custom configuration:
     * <ol>
     * <li>Create simulator with default configuration</li>
     * <li>Update the local copy of the configuration</li>
     * <li>Send the update via the update method</li>
     * </ol>
     * @param id Simulator ID
     * @param user User creating Simulator.  Same as TestSession in Toolkit UI. The simulator ID must be unique for this user.
     * @param environmentName Environment defines Affinity Domain coding schemes and TLS certificate for use with client.
     * @return Simulator configuration.
     * @throws ToolkitServiceException if anything goes wrong.
     */
    public DocumentRecipient createDocumentRecipient(String id, String user, String environmentName) throws ToolkitServiceException {
        XdrDocumentRecipient act = new XdrDocumentRecipient();
        act.engine = engine;
        act.config = engine.create(id, user, SimulatorActorType.DOCUMENT_RECIPIENT, environmentName);
        return act;
    }

    /**
     * Create new Document Registry/Repository simulator with default configuration.
     * To create a simulator with
     * custom configuration:
     * <ol>
     * <li>Create simulator with default configuration</li>
     * <li>Update the local copy of the configuration</li>
     * <li>Send the update via the update method</li>
     * </ol>
     * @param id Simulator ID
     * @param user User creating Simulator.  Same as TestSession in Toolkit UI. The simulator ID must be unique for this user.
     * @param environmentName Environment defines Affinity Domain coding schemes and TLS certificate for use with client.
     * @return Simulator configuration.
     * @throws ToolkitServiceException if anything goes wrong.
     */
    public DocumentRegRep createDocumentRegRep(String id, String user, String environmentName) throws ToolkitServiceException {
        XdsDocumentRegRep act = new XdsDocumentRegRep();
        act.engine = engine;
        act.config = engine.create(id, user, SimulatorActorType.REPOSITORY_REGISTRY, environmentName);
        return act;
    }

    public RespondingGateway createRespondingGateway(String id, String user, String environmentName) throws ToolkitServiceException {
        XcaRespondingGateway act = new XcaRespondingGateway();
        act.engine = engine;
        act.config = engine.create(id, user, SimulatorActorType.RESPONDING_GATEWAY, environmentName);
        return act;
    }

    /**
     * Update the configuration of a Simulator. Any properties that are passed in SimConfig that are
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
     * Delete a simulator. There is another call available using the SimId parameter type.  This
     * parameter type contains the raw ID and USER that are used here.  The two calls function identically.
     * If the simulator does not exist no error is returned.
     * @param id of simulator
     * @param user of simulator
     * @throws ToolkitServiceException if anything goes wrong.
     */
    public void delete(String id, String user) throws ToolkitServiceException {
        engine.delete(id, user);
    }

    private void delete(BasicSimParameters p) throws ToolkitServiceException {
        delete(p.getId(), p.getUser());
    }


    /**
     * Delete a simulator. There is another call available using separate raw ID and USER parameters.
     * USER and ID are components of the more formal SimId type.  The two calls function identically.
     * If the simulator does not exist no error is returned.
     * @param simId Simulator ID
     * @throws ToolkitServiceException if anything goes wrong
     */
    public void delete(SimId simId) throws ToolkitServiceException {
        engine.delete(simId);
    }

    /**
     * Get configuration of a Simulator.
     * @param simId simulator ID
     * @return simulator configuration
     * @throws ToolkitServiceException if anything goes wrong
     */
    public SimConfig get(SimId simId) throws ToolkitServiceException {
        return engine.get(simId);
    }

}
