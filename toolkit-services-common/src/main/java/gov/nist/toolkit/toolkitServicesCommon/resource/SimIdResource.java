package gov.nist.toolkit.toolkitServicesCommon.resource;


import gov.nist.toolkit.toolkitServicesCommon.SimId;
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Not for Public Use.
 */
@XmlRootElement
public class SimIdResource implements gov.nist.toolkit.toolkitServicesCommon.SimId {
    String user = null;
    String id = null;
    /**
     * If set, this overrides the default, which is user__id.
     */
    String fullId = null;
    String actorType = null;
    String environmentName = null;

    public SimIdResource() { }

    public SimIdResource(SimId simId) {
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
       if (fullId != null && fullId.length() > 0) return fullId;
        return user + "__" + id;
    }

    public void setFullId(String fid) {
       fullId = fid;}

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String describe() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        buf.append("user=");
        buf.append("\"");
        buf.append(user);
        buf.append("\"");
        buf.append(", id=");
        buf.append("\"");
        buf.append(id);
        buf.append("\"");
        buf.append(", actorType=");
        buf.append("\"");
        buf.append(actorType);
        buf.append("\"");
        buf.append(", environmentName=");
        buf.append("\"");
        buf.append(environmentName);
        buf.append("\"");

        buf.append("}");
        return buf.toString();
    }
}
