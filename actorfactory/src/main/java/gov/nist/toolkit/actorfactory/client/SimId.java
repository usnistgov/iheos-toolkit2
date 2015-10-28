package gov.nist.toolkit.actorfactory.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Created by bill on 9/15/15.
 */
public class SimId  implements Serializable, IsSerializable {

    static final String DEFAULT_USER = "nouser";
    static final String SEPARATOR = "__";

    public String user;
    public String id;
    String actorType;
    String environmenName;

    public SimId(String user, String id, String actorType, String environmentName) {
        this(user, id, actorType);
        this.environmenName = environmentName;
    }

    public SimId(String user, String id, String actorType) {
        this(user, id);
        this.actorType = actorType;
    }

    public SimId(String user, String id) {
        if (id.contains(SEPARATOR)) {
            String[] parts = id.split(SEPARATOR);
            this.user = parts[0];
            this.id = parts[1];
        } else {
            this.user = user;
            this.id = id;
        }
        this.id = cleanId(this.id);
    }

    public SimId(String id) {
        if (id.contains(SEPARATOR)) {
            String[] parts = id.split(SEPARATOR);
            this.user = parts[0];
            this.id = parts[1];
        } else {
            this.user = DEFAULT_USER;
            this.id = id;
        }
        this.id = cleanId(this.id);
    }

    public SimId() {}

    public boolean equals(SimId simId) {
        return this.user.equals(simId.user) && this.id.equals(simId.id);
    }

    public String toString() { return user + SEPARATOR + id; }

    public String validateState() {
        StringBuilder buf = new StringBuilder();

        if (user == null || user.equals("")) buf.append("No user specified\n");
        if (id == null || id.equals("")) buf.append("No id specified\n");
        if (actorType == null || actorType.equals("")) buf.append("No actorType specified\n");
        if (environmenName == null || environmenName.equals("")) buf.append("No environmentName specified");

        if (buf.length() == 0) return null;   // no errors
        return buf.toString();
    }

    String cleanId(String id) { return id.replaceAll("\\.", "_"); }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getEnvironmentName() {
        return environmenName;
    }

    public void setEnvironmenName(String environmenName) {
        this.environmenName = environmenName;
    }

    public String getUser() {
        return user;
    }

    public String getId() {
        return id;
    }

    public boolean isUser(String user) {
        return user != null && user.equals(this.user);
    }
    public boolean isValid() { return (!isEmpty(user)) && (!isEmpty(id)); }
    boolean isEmpty(String x) { return x == null || x.trim().equals(""); }
}
