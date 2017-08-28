package gov.nist.toolkit.toolkitApi;


import gov.nist.toolkit.configDatatypes.server.SimulatorActorType;

/**
 * Not for Public Use.
 */
public class BasicSimParameters {
    private String id;
    private String user;
    private SimulatorActorType actorType;
    private String environmentName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public SimulatorActorType getActorType() {
        return actorType;
    }

    public void setActorType(SimulatorActorType actorType) {
        this.actorType = actorType;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
