package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 *
 */
@XmlRootElement
public class RawSendRequestResource extends SimIdResource implements RawSendRequest {
    String transactionName;
    boolean tls = false;
    String metadata;
    List<String> extraHeaders = new ArrayList<>();
    Map<String, Document> documents = new HashMap<>();

    public RawSendRequestResource() {}

    public RawSendRequestResource(SimId simId) {
        setUser(simId.getUser());
        setId(simId.getId());
        setActorType(simId.getActorType());
        setEnvironmentName(simId.getEnvironmentName());
    }

    @Override
    public void addDocument(String id, Document doc) {
        documents.put(id, doc);
    }

    @Override
    public String getTransactionName() {
        return transactionName;
    }

    @Override
    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    @Override
    public boolean isTls() {
        return tls;
    }

    @Override
    public void setTls(boolean tls) {
        this.tls = tls;
    }

    @Override
    public String getMetadata() {
        return metadata;
    }

    @Override
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public List<String> getExtraHeaders() {
        return extraHeaders;
    }

    @Override
    public void addExtraHeader(String header) { extraHeaders.add(header); }

    public void setExtraHeaders(List<String> extraHeaders) {
        this.extraHeaders = extraHeaders;
    }

    @Override
    public Map<String, Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, Document> documents) {
        this.documents = documents;
    }
}
