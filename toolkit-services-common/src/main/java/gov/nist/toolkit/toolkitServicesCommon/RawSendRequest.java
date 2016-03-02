package gov.nist.toolkit.toolkitServicesCommon;

import gov.nist.toolkit.toolkitServicesCommon.resource.DocumentResource;

import java.util.Collection;
import java.util.Map;

/**
 * Send Request.
 * Raw means that the user provides the SOAP Header,
 * SOAP Body and document map to send.  No updates are done the the metadata.  It is sent as provided.
 */
public interface RawSendRequest extends SimId {
    /**
     * Add a Document to the send request. The id must match the id attribute on an ExtrinsicObject
     * in the metadata.
     * @param id
     * @param doc
     */
    void addDocument(String id, DocumentResource doc);

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
     * Metadata as a String.  It must be well formed XML (pass an XML parse). This will be the XML child element of
     * the SOAPBody element.  For a Provide and Register transaction the top element will be
     * the ProvideAndRegisterDocumentSetRequest element.
     * @param metadata XML
     */
    void setMetadata(String metadata);

    Collection<String> getExtraHeaders();

    /**
     * An extra header is a well formed XML element that will be added to the SOAPHeader of the sent message.
     * @param extraHeader well formed XML
     */
    void addExtraHeader(String extraHeader);

    /**
     * Get map of id to Document.  The id is the is attribute value in a Document Sharing ExtrinsicObject.
     * @return the map.
     */
    Map<String, DocumentResource> getDocuments();

//    void setDocuments(Map<String, Document> documents);
}
