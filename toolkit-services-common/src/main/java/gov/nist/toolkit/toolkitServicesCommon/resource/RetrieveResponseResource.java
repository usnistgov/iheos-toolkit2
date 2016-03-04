package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class RetrieveResponseResource implements RetrieveResponse {
    String mimeType;
    byte[] documentContents;

    public RetrieveResponseResource() {}

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getDocumentContents() {
        return documentContents;
    }

    public void setDocumentContents(byte[] documentContents) {
        this.documentContents = documentContents;
    }
}
