package gov.nist.toolkit.actortransaction.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public enum IheItiProfile  implements IsSerializable, Serializable {
    XDS("xds"),
    MHD("mhd"),
    XCA_I("xca-i"),
    XDS_I("xds-i"),
    FHIRINIT("fhir-init");

    private String code;

    IheItiProfile() {
        this("xds");
    }

    IheItiProfile(String code) {
        this.code = code;
    }

    static public IheItiProfile find(String s) {
        if (s == null || "".equals(s)) return XDS;
        for (IheItiProfile p : values()) {
            if (s.equals(p.code)) return p;
            try {
                if (p == IheItiProfile.valueOf(s)) return p;
            } catch (IllegalArgumentException e) {
                // continue;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }

    public boolean equals(IheItiProfile p) {
       return (p.toString().equals(this.toString()));
    }

    public boolean equals(String s) {
        return (this.toString().equals(s));
    }

    public String getCode() { return code; }


}
