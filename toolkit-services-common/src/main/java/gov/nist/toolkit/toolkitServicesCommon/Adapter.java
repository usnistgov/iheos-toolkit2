package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Not for Public Use.
 */
public class Adapter extends XmlAdapter<List<KeyValue>, Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(List<KeyValue> v) throws Exception {
        Map<String, String> map = new HashMap<String, String>(v.size());
        for (KeyValue keyValue : v) {
            map.put(keyValue.key, keyValue.value);
        }
        return map;
    }

    @Override
    public List<KeyValue> marshal(Map<String, String> v) throws Exception {
        Set<String> keys = v.keySet();
        List<KeyValue> results = new ArrayList<KeyValue>(v.size());
        for (String key : keys) {
            results.add(new KeyValue(key, v.get(key)));
        }
        return results;
    }
}