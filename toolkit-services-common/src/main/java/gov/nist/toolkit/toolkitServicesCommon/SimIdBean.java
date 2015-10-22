package gov.nist.toolkit.toolkitServicesCommon;


import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class SimIdBean implements gov.nist.toolkit.toolkitServicesCommon.SimId {
    String user = null;
    String id = null;
    String actorType = null;
    String environmentName = null;

    public SimIdBean() { }

    public SimIdBean(SimId simId) {
        user = simId.getUser();
        id = simId.getId();
        actorType = simId.getActorType();
        environmentName = simId.getEnvironmentName();
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SimId asSimId() { return ToolkitFactory.newSimId(user, id, actorType, environmentName); }

    @Override
    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    @Override
    public String getEnvironmentName() {
        return environmentName;
    }

    @Override
    public String getFullId() {
        return user + "__" + id;
    }

    public void setFullId(String x) {}

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
