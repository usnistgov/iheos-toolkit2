package gov.nist.toolkit.configDatatypes.client;

import java.io.Serializable;

/**
 *
 */
public class Pid implements Serializable {
    String ad = null;
    String id = null;
    String extra = "";

    public Pid(String ad, String id) {
        if (ad == null) this.ad = ad;
        else if (ad.equals("null")) this.ad = null;
        else this.ad = ad;
        if (id == null) this.id = id;
        else if (id.equals("null")) this.id = null;
        else this.id = id;
        trim();
    }

    public Pid() {}

    public void setExtra(String extra) { this.extra = extra.trim(); }

    private void trim() {
        if (ad != null) ad = ad.trim();
        if (id != null) id = id.trim();
    }

    public String getAd() { return ad; }
    public String getId() { return id; }
    public void setId(String id){this.id=id;}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pid)) return false;
        Pid p = (Pid) o;
        if (ad == null) {
            if (p.ad != null) return false;
        } else {
            if (!ad.equals(p.ad)) return false;
        }
        if (id == null) {
            if (p.id != null) return false;
        } else {
            if (!id.equals(p.id)) return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return 41 + ad.hashCode() + id.hashCode();
    }

    public boolean validate() {
        if (!isAdProperOid()) return false;
        return isIdProper();
    }

    public boolean isAdProperOid() {
        if (ad == null) return false;
        for (int i=0; i<ad.length(); i++) {
            char c = ad.charAt(i);
            if (c == '.') continue;
            if ("1234567890".indexOf(c) != -1) continue;
            return false;
        }
        return true;
    }

    boolean isIdProper() {
        if (id == null) return false;
        for (int i=0; i<id.length(); i++) {
            char c = id.charAt(i);
            if (c == '&') return false;
            if (c == '^') return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + "^^^&" + ad + "&ISO";
    }

    // full information - parsable by PidBuilder.createPid()
    // can be used in UIs
    public String asParsableString() {
        if (extra == null)
            return toString();
        return toString() + " "+PidBuilder.SEPARATOR+" " + extra;
    }

    // String representation - as required in protocols
    public String asString() { return toString(); }

    public String getExtra() {
        return extra;
    }
}
