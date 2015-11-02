package gov.nist.toolkit.soap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DocumentMap {
    Map<String, Document> documents = new HashMap<>();

    public DocumentMap() {}

    public void addDocument(String id, Document doc) {
        documents.put(id, doc);
    }

    public Collection<String> getIds() { return documents.keySet(); }

    public Document getDocument(String id) { return documents.get(id); }

    public Map<String, Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, Document> documents) {
        this.documents = documents;
    }

}
