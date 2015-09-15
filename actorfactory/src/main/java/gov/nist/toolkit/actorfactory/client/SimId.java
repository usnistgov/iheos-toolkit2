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

    public String toString() { return user + SEPARATOR + id; }

    String cleanId(String id) { return id.replaceAll("\\.", "_"); }
}
