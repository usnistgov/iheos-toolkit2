package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.SimId;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class SimIdBean {
    String user = null;
    String id = null;
    String actorType = null;
    String environmentName = null;

    public SimIdBean() { }

    public SimIdBean(SimId simId) {
        user = simId.user;
        id = simId.id;
        actorType = simId.getActorType();
        environmentName = simId.getEnvironmenName();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SimId asSimId() { return new SimId(user, id, actorType, environmentName); }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
