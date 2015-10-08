package gov.nist.toolkit.toolkitServices;

import gov.nist.toolkit.actorfactory.client.SimId;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class SimIdBean {
    String user;
    String id;

    public SimIdBean() { }

    public SimIdBean(SimId simId) {
        user = simId.user;
        id = simId.id;
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

    public SimId asSimId() { return new SimId(user, id); }
}
