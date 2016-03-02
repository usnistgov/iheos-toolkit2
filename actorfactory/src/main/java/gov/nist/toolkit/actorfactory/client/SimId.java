package gov.nist.toolkit.actorfactory.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class SimId implements Serializable, IsSerializable {

    static final String DEFAULT_USER = "nouser";
    static final String SEPARATOR = "__";
    static final String SLASH = "/";

    public String user;
    public String id;
    String actorType;
    String environmentName;

    // server only
    public SimId(String user, String id, String actorType, String environmentName) throws BadSimIdException {
        this(user, id, actorType);
        this.environmentName = environmentName;
    }

    // client only
    public SimId(String user, String id, String actorType) throws BadSimIdException {
        this(user, id);
        this.actorType = actorType;
    }

    // client and server
    public SimId(String user, String id) throws BadSimIdException {
        build(user, id);
    }
//    public SimId(String user, String id) {
//        if (id.contains(SEPARATOR)) {
//            String[] parts = id.split(SEPARATOR);
//            this.user = parts[0];
//            this.id = parts[1];
//        } else {
//            this.user = user;
//            this.id = id;
//        }
//        this.id = cleanId(this.id);
//    }

    // server
    public SimId(String id) throws BadSimIdException {
        if (id.contains(SEPARATOR)) {
            String[] parts = id.split(SEPARATOR, 2);
            build(parts[0], parts[1]);
        } else {
            build(DEFAULT_USER, id);
        }
    }

    void build(String user, String id) throws BadSimIdException {
        user = cleanId(user);
        id = cleanId(id);
        if (user.contains(SEPARATOR)) throw new BadSimIdException(SEPARATOR + " is illegal in simulator user name");
        if (user.contains(SLASH)) throw new BadSimIdException(SLASH + " is illegal in simulator user name");
        if (id.contains(SLASH)) throw new BadSimIdException(SLASH + " is illegal in simulator id");
        this.user = user;
        this.id = id;
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
        if (environmentName == null || environmentName.equals("")) buf.append("No environmentName specified");

        if (buf.length() == 0) return null;   // no errors
        return buf.toString();
    }

    String cleanId(String id) { return id.replaceAll("\\.", "_").toLowerCase(); }

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
