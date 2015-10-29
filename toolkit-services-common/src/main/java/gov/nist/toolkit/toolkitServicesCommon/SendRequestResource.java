package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@XmlRootElement
public class SendRequestResource extends SimIdResource implements SendRequest {
    String transactionName;
    boolean tls = false;
    String metadata;
    String extraHeaders = null;
    Map<String, Document> documents = new HashMap<>();

    public SendRequestResource() {}


    public void addDocument(String id, Document doc) {
        documents.put(id, doc);
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public boolean isTls() {
        return tls;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getExtraHeaders() {
        return extraHeaders;
    }

    public void setExtraHeaders(String extraHeaders) {
        this.extraHeaders = extraHeaders;
    }

    public Map<String, Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, Document> documents) {
        this.documents = documents;
    }
}
