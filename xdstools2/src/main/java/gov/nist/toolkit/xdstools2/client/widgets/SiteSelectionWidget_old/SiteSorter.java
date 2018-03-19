package gov.nist.toolkit.xdstools2.client.widgets.SiteSelectionWidget_old;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SiteSorter {
    class Trans {
        String tls = null;
        String notls = null;
    }

    // transaction name ==> Trans
    private Map<String, Trans> map = new HashMap<>();

    public void add(String transName, String endpoint) {
        Trans trans = map.get(transName);
        boolean isNew = trans == null;
        if (trans == null)
            trans = new Trans();
        if (endpoint.startsWith("https"))
            trans.tls = endpoint;
        else
            trans.notls = endpoint;
        if (isNew)
            map.put(transName, trans);
    }

    public String getTls(String trans) {
        Trans t = map.get(trans);
        if (t == null) return null;
        return t.tls;
    }

    public String getNoTls(String trans) {
        Trans t = map.get(trans);
        if (t == null) return null;
        return t.notls;
    }

    public boolean hasEndpoints(String trans) {
        Trans t = map.get(trans);
        if (t == null) return false;
        String reg = t.notls;
        String tls = t.tls;
        if (reg != null && !reg.equals("")) return true;
        if (tls != null && !tls.equals("")) return true;
        return false;
    }
}
