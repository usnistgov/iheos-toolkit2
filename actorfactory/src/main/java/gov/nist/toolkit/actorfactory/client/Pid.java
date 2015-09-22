package gov.nist.toolkit.actorfactory.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Created by bill on 9/21/15.
 */
public class Pid implements Serializable, IsSerializable {
    String ad = null;
    String id = null;

    public Pid(String ad, String id) {
        this.ad = ad;
        this.id = id;
    }

    public Pid() {}

    public String getAd() { return ad; }
    public String getId() { return id; }

    public String toString() {
        return id + "^^^&" + ad + "&ISO";
    }
}
