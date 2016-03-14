package gov.nist.toolkit.toolkitServicesCommon;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class HeaderList {
    Map<String, List<String>> hdrs;

    public List<String> values(String key) {
        List<String> vals = hdrs.get(key);
        if (vals == null) {
            vals = new ArrayList<String>();
            hdrs.put(key, vals);
        }
        return vals;
    }

    public void add(String key, String value) {
        values(key).add(value);
    }

    public Set<String> keys() {
        return hdrs.keySet();
    }

    public Response.ResponseBuilder addHeaders(Response.ResponseBuilder builder) {
        for (String key : keys()) {
            for (String value : values(key)) {
                builder.header(key, value);
            }
        }
        return builder;
    }
}
