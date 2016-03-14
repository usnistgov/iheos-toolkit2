package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.RawSendRequest;
import gov.nist.toolkit.toolkitServicesCommon.SimId;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@XmlRootElement
public class RawSendRequestResource extends SimIdResource implements RawSendRequest {
    String transactionName;
    boolean tls = false;
    String metadata;
    List<String> extraHeaders = new ArrayList<String>();
    Map<String, DocumentResource> documents = new HashMap<String, DocumentResource>();

    public RawSendRequestResource() {}

    public RawSendRequestResource(SimId simId) {
        setUser(simId.getUser());
        setId(simId.getId());
        setActorType(simId.getActorType());
        setEnvironmentName(simId.getEnvironmentName());
    }

    @Override
    public void addDocument(String id, DocumentResource doc) {
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
    public Map<String, DocumentResource> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, DocumentResource> documents) {
        this.documents = documents;
    }
}
