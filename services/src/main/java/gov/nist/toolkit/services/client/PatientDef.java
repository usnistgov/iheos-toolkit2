package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class PatientDef implements Serializable, IsSerializable {
    public String pid;
    public String given;
    public String family;
    public String url;

    public PatientDef() {}

    public PatientDef(String pid, String given, String family, String url) {
        this.pid = pid;
        this.given = given;
        this.family = family;
        this.url = url;
    }
}