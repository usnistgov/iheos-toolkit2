package gov.nist.toolkit.tookitApi;

/**
 *
 */
public class RetrieveResponse  {
    String mimeType;
    byte[] documentContents;

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
