package gov.nist.toolkit.registrymsg.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class RetrievedDocumentsModel {
    Map<String, RetrievedDocumentModel> map = new HashMap<>();

    public Map<String, RetrievedDocumentModel> getMap() {
        return map;
    }

    public RetrievedDocumentsModel setMap(Map<String, RetrievedDocumentModel> map) {
        this.map = map;
        return this;
    }

    public void put(String id, RetrievedDocumentModel item) {
        map.put(id, item);
    }

    public RetrievedDocumentModel get(String id) {
        return map.get(id);
    }

    public int size() { return map.size(); }

    public Set<String> keySet() { return map.keySet(); }

    public Collection<RetrievedDocumentModel> values() {
        assignCids(); return map.values();
    }

    public void add(RetrievedDocumentModel item) {
        put(item.getDocUid(), item);
    }

    void assignCids() {
        int i = 1;
        for (RetrievedDocumentModel item : map.values()) {
            if (item.getCid() != null) return;
            item.setCid(String.format("CID%d", i));
            i++;
        }
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetrievedDocumentsModel that = (RetrievedDocumentsModel) o;

        return map.equals(that.map);

    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
