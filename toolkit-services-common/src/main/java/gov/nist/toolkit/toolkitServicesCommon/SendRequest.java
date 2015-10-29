package gov.nist.toolkit.toolkitServicesCommon;

import java.util.Collection;
import java.util.Map;

/**
 * Created by bill on 10/28/15.
 */
public interface SendRequest  extends SimId {
    /**
     * Add a Document to the send request. The id must match the id attribute on an ExtrinsicObject
     * in the metadata.
     * @param id
     * @param doc
     */
    void addDocument(String id, Document doc);

    String getTransactionName();

    /**
     * Id of the transaction to send.
     * @param transactionName transaction id
     */
    void setTransactionName(String transactionName);

    boolean isTls();

    /**
     * Send over TLS?
     * @param tls -
     */
    void setTls(boolean tls);

    String getMetadata();

    /**
     * Metadata as a String.  It must be well formed XML (pass an XML parse).
     * @param metadata XML
     */
    void setMetadata(String metadata);

    Collection<String> getExtraHeaders();

    /**
     * An extra header is a well formed XML element that will be added to the SOAPHeader of the sent message.
     * @param extraHeader well formed XML
     */
    void addExtraHeader(String extraHeader);

    Map<String, Document> getDocuments();

//    void setDocuments(Map<String, Document> documents);
}
